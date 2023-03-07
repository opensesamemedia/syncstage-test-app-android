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

)***REMOVED***
    private val TAG = "Detection5G"
    private var connectivityManagerCallback: ConnectivityManager.NetworkCallback? = null
    private var phoneStateListener: PhoneStateListener? = null
    private var telephonyCallback: TelephonyCallback? = null
    private var started: Boolean = false

    init ***REMOVED***
        // get initial value from the old API
        onNetworkTypeChange("Init old api: $***REMOVED***getNetworkTypeOldAPI(ctx)***REMOVED***")
    ***REMOVED***

    private fun isAirplaneModeOn(context: Context): Boolean ***REMOVED***
        return Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.AIRPLANE_MODE_ON, 0
            ) !== 0
    ***REMOVED***

    private fun isSimInCorrectState(simState: Int): Boolean***REMOVED***
        return when (simState) ***REMOVED***
            TelephonyManager.SIM_STATE_ABSENT -> false
            TelephonyManager.SIM_STATE_NETWORK_LOCKED -> false
            TelephonyManager.SIM_STATE_PIN_REQUIRED -> false
            TelephonyManager.SIM_STATE_PUK_REQUIRED -> false
            TelephonyManager.SIM_STATE_READY -> true
            TelephonyManager.SIM_STATE_UNKNOWN -> false
            else -> false
        ***REMOVED***
    ***REMOVED***

    fun isSIMInsertedAndNotAirplaneModeOn(): Boolean ***REMOVED***
        val simStateMain: Int = telephonyManager.getSimState(0)
        val simStateSecond: Int = telephonyManager.getSimState(1)

        val simInserted = isSimInCorrectState(simStateMain) || isSimInCorrectState(simStateSecond) || ! isAirplaneModeOn(ctx)
        return simInserted
    ***REMOVED***

    fun startListenNetworkType() ***REMOVED***
        if (started)***REMOVED***
            return
        ***REMOVED***else***REMOVED***
            started = true
        ***REMOVED***
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)***REMOVED***
            Log.d(TAG, "5G network probing will not be activated due to too low sdk version.")
        ***REMOVED*** else***REMOVED***

            listenUnlimitedNetwork ***REMOVED***
                Log.d(TAG, "listenUnlimitedNetwork: Unlimited network: $***REMOVED***it***REMOVED***")
            ***REMOVED***
            listenNewRadio(
                onCellInfoCallback = ***REMOVED***
                    val text = when (it) ***REMOVED***
                        is CellInfoLte -> """LTE connection band：$***REMOVED***it.cellIdentity.bands.map ***REMOVED*** it.toString() ***REMOVED******REMOVED*** ($***REMOVED***it.cellIdentity.earfcn***REMOVED***)"""
                        is CellInfoNr -> """5G connecting band：$***REMOVED***(it.cellIdentity as CellIdentityNr).bands.map ***REMOVED*** it.toString() ***REMOVED******REMOVED*** ($***REMOVED***(it.cellIdentity as CellIdentityNr).nrarfcn***REMOVED***) $***REMOVED***if ((it.cellIdentity as CellIdentityNr).nrarfcn > 2054166) "Connecting to millimeter waves" else "Connecting to Sub-6"***REMOVED***"""
                        else -> ""
                    ***REMOVED***
                    Log.i(TAG, "onCellInfoCallback: $text")
              ***REMOVED***
                onDisplayInfoCallback = ***REMOVED***
                    var text =  when (it.overrideNetworkType) ***REMOVED***
                        TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_LTE_ADVANCED_PRO -> "LTE Advanced Pro（5Ge）"
                        TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_LTE_CA -> "LTE carrier aggregation"
                        TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_NSA -> "5G Sub-6 network (NSA)"
                        TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_NSA_MMWAVE -> "Millimeter wave network (not recommended)"
                        TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_ADVANCED -> "5G Millimeter-wave (or equivalent) network"
                        else -> "callback unkn: $***REMOVED***it.overrideNetworkType***REMOVED*** $***REMOVED***getNetworkTypeOldAPI(ctx)***REMOVED***"
                    ***REMOVED***
                    if (isWifiConnected(ctx))***REMOVED***
                        text = "WIFI"
                    ***REMOVED***

                    Log.i(TAG, "onDisplayInfoCallback: $text")
                    onNetworkTypeChange(text)
              ***REMOVED***
                onAnchorBandCallback = ***REMOVED***
                    val text = if (it) "Anchor band is connected" else "Not in 4G connection or anchor band."
                    Log.i(TAG, "onAnchorBandCallback: $text")
                ***REMOVED***
            )
        ***REMOVED***
    ***REMOVED***

    private fun listenNewRadio(
        onCellInfoCallback: (CellInfo) -> Unit,
        onDisplayInfoCallback: (TelephonyDisplayInfo) -> Unit,
        onAnchorBandCallback: (Boolean) -> Unit,

        ) ***REMOVED***
        // onCellInfoChanged onDisplayInfoChanged Temporarily keep the result of
        var tempCellInfo: CellInfo? = null
        var tempTelephonyDisplayInfo: TelephonyDisplayInfo? = null

        // Send if it is an anchor band
        fun checkAnchorBand() ***REMOVED***
            if (tempCellInfo == null && tempTelephonyDisplayInfo == null) return
            // If CellInfo is LTE and the icon actually displayed is 5G, the anchor band
            val isAnchorBand = tempCellInfo is CellInfoLte && tempTelephonyDisplayInfo?.overrideNetworkType == TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_NSA
            onAnchorBandCallback(isAnchorBand)
        ***REMOVED***

        // Android 12
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ***REMOVED***
            telephonyCallback = object : TelephonyCallback(), TelephonyCallback.DisplayInfoListener, TelephonyCallback.CellInfoListener ***REMOVED***
                override fun onCellInfoChanged(cellInfo: MutableList<CellInfo>) ***REMOVED***
                    try ***REMOVED***
                        if (cellInfo.size >0) ***REMOVED***
                            onCellInfoCallback(cellInfo[0])
                            tempCellInfo = cellInfo[0]
                            checkAnchorBand()
                        ***REMOVED***else***REMOVED***
                            Log.w(TAG, "Cell info size = 0, cannot execute onCellInfoChanged.")
                        ***REMOVED***
                    ***REMOVED***catch (e: java.lang.NullPointerException)***REMOVED***
                        Log.w(TAG, "Error in onCellInfoChanged: $***REMOVED***e***REMOVED***")
                    ***REMOVED***

                ***REMOVED***

                override fun onDisplayInfoChanged(telephonyDisplayInfo: TelephonyDisplayInfo) ***REMOVED***
                    onDisplayInfoCallback(telephonyDisplayInfo)
                    tempTelephonyDisplayInfo = telephonyDisplayInfo
                    checkAnchorBand()
                ***REMOVED***
            ***REMOVED***
            if (telephonyCallback != null && telephonyManager != null) ***REMOVED***
                try ***REMOVED***
                    telephonyManager.registerTelephonyCallback(
                        ContextCompat.getMainExecutor(ctx),
                        telephonyCallback!!
                    )
                ***REMOVED***catch(e: SecurityException)***REMOVED***
                    Log.e(TAG, "Could not register telephony callback. Insufficient permissions. $***REMOVED***e.toString()***REMOVED***")
                ***REMOVED***
            ***REMOVED***else***REMOVED***
                Log.w(TAG, "Could not register telephonyCallback")
            ***REMOVED***
        ***REMOVED*** else ***REMOVED***
            phoneStateListener = object : PhoneStateListener() ***REMOVED***

                 @SuppressLint("MissingPermission")
                 override fun onCellInfoChanged(cellInfo: MutableList<CellInfo>?) ***REMOVED***
                     try ***REMOVED***
                         if (cellInfo != null)***REMOVED***
                             super.onCellInfoChanged(cellInfo)
                             cellInfo[0].let ***REMOVED***
                                 onCellInfoCallback(it)
                             ***REMOVED***
                         ***REMOVED***
                     ***REMOVED***catch (e: java.lang.NullPointerException)***REMOVED***
                         Log.w(TAG, "Error in onCellInfoChanged: $***REMOVED***e***REMOVED***")
                     ***REMOVED***
                 ***REMOVED***

                @SuppressLint("MissingPermission")
                override fun onDisplayInfoChanged(telephonyDisplayInfo: TelephonyDisplayInfo) ***REMOVED***
                    super.onDisplayInfoChanged(telephonyDisplayInfo)
                    onDisplayInfoCallback(telephonyDisplayInfo)
                ***REMOVED***
            ***REMOVED***
            telephonyManager.listen(phoneStateListener!!, PhoneStateListener.LISTEN_DISPLAY_INFO_CHANGED or PhoneStateListener.LISTEN_CELL_INFO)
        ***REMOVED***
    ***REMOVED***

    private fun listenUnlimitedNetwork(onResult: (Boolean) -> Unit) ***REMOVED***
        connectivityManagerCallback = object : ConnectivityManager.NetworkCallback() ***REMOVED***
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) ***REMOVED***
                super.onCapabilitiesChanged(network, networkCapabilities)
                // If you have an unlimited plan = true
                val isUnlimited = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED) ||
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_TEMPORARILY_NOT_METERED)
                onResult(isUnlimited)
            ***REMOVED***
        ***REMOVED***
        connectivityManager.registerDefaultNetworkCallback(connectivityManagerCallback!!)
    ***REMOVED***

    fun destroy()***REMOVED***
//        if (!isSIMInsertedAndNotAirplaneModeOn())***REMOVED***
//            Log.i(TAG, "Sim card not inserted.");
//            return
//        ***REMOVED***
        connectivityManagerCallback?.let ***REMOVED*** connectivityManager.unregisterNetworkCallback(it) ***REMOVED***
        phoneStateListener?.let ***REMOVED*** telephonyManager.listen(it, PhoneStateListener.LISTEN_NONE) ***REMOVED***
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ***REMOVED***
            telephonyCallback?.let ***REMOVED*** telephonyManager.unregisterTelephonyCallback(it) ***REMOVED***
        ***REMOVED***
        started = false
    ***REMOVED***
***REMOVED***
