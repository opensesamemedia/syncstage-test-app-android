package media.opensesame.networkutils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import android.telephony.*
import android.util.Log
import androidx.core.content.ContextCompat


// === 5G listeners based on https://github.com/takusan23/NewRadioAPIChecker
// Some updates might be needed for android 12 (api 31) target build https://stackoverflow.com/a/71789261
class Detection5G(
    val ctx: Context,
    val connectivityManager: ConnectivityManager,
    val onNetworkTypeChange: (String) -> Unit,
    val telephonyManager: TelephonyManager,

    ) {
    private val TAG = "Detection5G"
    private var connectivityManagerCallback: ConnectivityManager.NetworkCallback? = null
    private var phoneStateListener: PhoneStateListener? = null
    private var telephonyCallback: TelephonyCallback? = null
    private var started: Boolean = false

    init {
        // get initial value from the old API
        onNetworkTypeChange("Init old api: ${getNetworkTypeOldAPI(ctx)}")
    }

    private fun isAirplaneModeOn(context: Context): Boolean {
        return Settings.Global.getInt(
            context.contentResolver,
            Settings.Global.AIRPLANE_MODE_ON, 0
        ) !== 0
    }

    private fun isSimInCorrectState(simState: Int): Boolean {
        return when (simState) {
            TelephonyManager.SIM_STATE_ABSENT -> false
            TelephonyManager.SIM_STATE_NETWORK_LOCKED -> false
            TelephonyManager.SIM_STATE_PIN_REQUIRED -> false
            TelephonyManager.SIM_STATE_PUK_REQUIRED -> false
            TelephonyManager.SIM_STATE_READY -> true
            TelephonyManager.SIM_STATE_UNKNOWN -> false
            else -> false
        }
    }

    fun isSIMInsertedAndNotAirplaneModeOn(): Boolean {
        val simStateMain: Int = telephonyManager.getSimState(0)
        val simStateSecond: Int = telephonyManager.getSimState(1)

        val simInserted =
            isSimInCorrectState(simStateMain) || isSimInCorrectState(simStateSecond) || !isAirplaneModeOn(
                ctx
            )
        return simInserted
    }

    fun startListenNetworkType() {
        if (started) {
            return
        } else {
            started = true
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            Log.d(TAG, "5G network probing will not be activated due to too low sdk version.")
        } else {

            listenUnlimitedNetwork {
                Log.d(TAG, "listenUnlimitedNetwork: Unlimited network: ${it}")
            }
            listenNewRadio(
                onCellInfoCallback = {
                    val text = when (it) {
                        is CellInfoLte -> """LTE connection band：${it.cellIdentity.bands.map { it.toString() }} (${it.cellIdentity.earfcn})"""
                        is CellInfoNr -> """5G connecting band：${(it.cellIdentity as CellIdentityNr).bands.map { it.toString() }} (${(it.cellIdentity as CellIdentityNr).nrarfcn}) ${if ((it.cellIdentity as CellIdentityNr).nrarfcn > 2054166) "Connecting to millimeter waves" else "Connecting to Sub-6"}"""
                        else -> ""
                    }
                    Log.i(TAG, "onCellInfoCallback: $text")
                },
                onDisplayInfoCallback = {
                    var text = when (it.overrideNetworkType) {
                        TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_LTE_ADVANCED_PRO -> "LTE Advanced Pro（5Ge）"
                        TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_LTE_CA -> "LTE carrier aggregation"
                        TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_NSA -> "5G Sub-6 network (NSA)"
                        TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_NSA_MMWAVE -> "Millimeter wave network (not recommended)"
                        TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_ADVANCED -> "5G Millimeter-wave (or equivalent) network"
                        else -> "callback unkn: ${it.overrideNetworkType} ${getNetworkTypeOldAPI(ctx)}"
                    }
                    if (isWifiConnected(ctx)) {
                        text = "WIFI"
                    }

                    Log.i(TAG, "onDisplayInfoCallback: $text")
                    onNetworkTypeChange(text)
                },
                onAnchorBandCallback = {
                    val text =
                        if (it) "Anchor band is connected" else "Not in 4G connection or anchor band."
                    Log.i(TAG, "onAnchorBandCallback: $text")
                }
            )
        }
    }

    private fun listenNewRadio(
        onCellInfoCallback: (CellInfo) -> Unit,
        onDisplayInfoCallback: (TelephonyDisplayInfo) -> Unit,
        onAnchorBandCallback: (Boolean) -> Unit,

        ) {
        // onCellInfoChanged onDisplayInfoChanged Temporarily keep the result of
        var tempCellInfo: CellInfo? = null
        var tempTelephonyDisplayInfo: TelephonyDisplayInfo? = null

        // Send if it is an anchor band
        fun checkAnchorBand() {
            if (tempCellInfo == null && tempTelephonyDisplayInfo == null) return
            // If CellInfo is LTE and the icon actually displayed is 5G, the anchor band
            val isAnchorBand =
                tempCellInfo is CellInfoLte && tempTelephonyDisplayInfo?.overrideNetworkType == TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_NSA
            onAnchorBandCallback(isAnchorBand)
        }

        // Android 12
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telephonyCallback = object : TelephonyCallback(), TelephonyCallback.DisplayInfoListener,
                TelephonyCallback.CellInfoListener {
                override fun onCellInfoChanged(cellInfo: MutableList<CellInfo>) {
                    try {
                        if (cellInfo.size > 0) {
                            onCellInfoCallback(cellInfo[0])
                            tempCellInfo = cellInfo[0]
                            checkAnchorBand()
                        } else {
                            Log.w(TAG, "Cell info size = 0, cannot execute onCellInfoChanged.")
                        }
                    } catch (e: java.lang.NullPointerException) {
                        Log.w(TAG, "Error in onCellInfoChanged: ${e}")
                    }

                }

                override fun onDisplayInfoChanged(telephonyDisplayInfo: TelephonyDisplayInfo) {
                    onDisplayInfoCallback(telephonyDisplayInfo)
                    tempTelephonyDisplayInfo = telephonyDisplayInfo
                    checkAnchorBand()
                }
            }
            if (telephonyCallback != null && telephonyManager != null) {
                try {
                    telephonyManager.registerTelephonyCallback(
                        ContextCompat.getMainExecutor(ctx),
                        telephonyCallback!!
                    )
                } catch (e: SecurityException) {
                    Log.e(
                        TAG,
                        "Could not register telephony callback. Insufficient permissions. ${e.toString()}"
                    )
                }
            } else {
                Log.w(TAG, "Could not register telephonyCallback")
            }
        } else {
            phoneStateListener = object : PhoneStateListener() {

                @SuppressLint("MissingPermission")
                override fun onCellInfoChanged(cellInfo: MutableList<CellInfo>?) {
                    try {
                        if (cellInfo != null) {
                            super.onCellInfoChanged(cellInfo)
                            cellInfo[0].let {
                                onCellInfoCallback(it)
                            }
                        }
                    } catch (e: java.lang.NullPointerException) {
                        Log.w(TAG, "Error in onCellInfoChanged: ${e}")
                    }
                }

                @SuppressLint("MissingPermission")
                override fun onDisplayInfoChanged(telephonyDisplayInfo: TelephonyDisplayInfo) {
                    super.onDisplayInfoChanged(telephonyDisplayInfo)
                    onDisplayInfoCallback(telephonyDisplayInfo)
                }
            }
            telephonyManager.listen(
                phoneStateListener!!,
                PhoneStateListener.LISTEN_DISPLAY_INFO_CHANGED or PhoneStateListener.LISTEN_CELL_INFO
            )
        }
    }

    private fun listenUnlimitedNetwork(onResult: (Boolean) -> Unit) {
        connectivityManagerCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                // If you have an unlimited plan = true
                val isUnlimited =
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED) ||
                            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_TEMPORARILY_NOT_METERED)
                onResult(isUnlimited)
            }
        }
        connectivityManager.registerDefaultNetworkCallback(connectivityManagerCallback!!)
    }

    fun destroy() {
//        if (!isSIMInsertedAndNotAirplaneModeOn()){
//            Log.i(TAG, "Sim card not inserted.");
//            return
//        }
        connectivityManagerCallback?.let { connectivityManager.unregisterNetworkCallback(it) }
        phoneStateListener?.let { telephonyManager.listen(it, PhoneStateListener.LISTEN_NONE) }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telephonyCallback?.let { telephonyManager.unregisterTelephonyCallback(it) }
        }
        started = false
    }
}
