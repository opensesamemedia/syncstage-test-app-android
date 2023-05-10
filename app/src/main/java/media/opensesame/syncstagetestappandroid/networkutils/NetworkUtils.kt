package media.opensesame.networkutils

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.telephony.CellInfo
import android.telephony.CellInfoLte
import android.telephony.SignalStrength
import android.telephony.TelephonyManager
import android.util.Log
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

val TAG = "NetworkUtils"

fun isLocationEnabled(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        // This is a new method provided in API 28
        val lm: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        lm.isLocationEnabled
    } else {
        // This was deprecated in API 28
        val mode: Int = Settings.Secure.getInt(
            context.contentResolver, Settings.Secure.LOCATION_MODE,
            Settings.Secure.LOCATION_MODE_OFF
        )
        mode != Settings.Secure.LOCATION_MODE_OFF
    }
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (connectivityManager != null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities: NetworkCapabilities? =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    return true
                }
            }
        } else {
            try {
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                    Log.i(TAG, "Network is available : true")
                    return true
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message ?: "")
            }
        }
    }
    Log.i(TAG, "Network is available : FALSE ")
    return false
}


fun isWifiConnected(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val info: NetworkInfo? = cm.activeNetworkInfo
    if (info != null) {
        return info.type === ConnectivityManager.TYPE_WIFI
    }
    return false
}


fun getNetworkTypeOldAPI(context: Context): String {
    if (isWifiConnected(context)) {
        return "WIFI"
    }

    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val info: NetworkInfo? = cm.activeNetworkInfo
    if (info == null || !info.isConnected) return "-" // not connected

    var networkTypeString = "?"

    if (info.type === ConnectivityManager.TYPE_MOBILE) {
        val networkType: Int = info.subtype
        networkTypeString = when (networkType) {
            TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN, TelephonyManager.NETWORK_TYPE_GSM -> "2G"
            TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP, TelephonyManager.NETWORK_TYPE_TD_SCDMA -> "3G"
            TelephonyManager.NETWORK_TYPE_LTE, TelephonyManager.NETWORK_TYPE_IWLAN, 19 -> "4G"
            TelephonyManager.NETWORK_TYPE_NR -> "5G"
            else -> "?"
        }
    }

    if (!isLocationEnabled(context)) {
        networkTypeString = "Mobile network"
    }

    return networkTypeString
}

fun getWifiStrength(context: Context): Int {
    val wifiManager: WifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val rssi = wifiManager.connectionInfo.rssi
    return rssi
}


@SuppressLint("MissingPermission")
fun getLTEsignalStrength(signalStrength: SignalStrength, tm: TelephonyManager): Int? {
    val cellInfoList: List<CellInfo>
    var cellSig: Int? = null
    var cellID: Int
    var cellPci: Int
    var cellTac = 0

    val ltestr = signalStrength.toString()
    val parts = ltestr.split(" ".toRegex()).toTypedArray()
    try {
        val cellSig2 = parts[9]
        cellInfoList = tm.allCellInfo
        for (cellInfo in cellInfoList) {
            if (cellInfo is CellInfoLte) {
                // cast to CellInfoLte and call all the CellInfoLte methods you need
                // gets RSRP cell signal strength:
                cellSig = cellInfo.cellSignalStrength.dbm

                // Gets the LTE cell identity: (returns 28-bit Cell Identity, Integer.MAX_VALUE if unknown)
                cellID = cellInfo.cellIdentity.ci

                // Gets the LTE PCI: (returns Physical Cell Id 0..503, Integer.MAX_VALUE if unknown)
                cellPci = cellInfo.cellIdentity.pci

                // Gets the LTE TAC: (returns 16-bit Tracking Area Code, Integer.MAX_VALUE if unknown)
                cellTac = cellInfo.cellIdentity.tac
            }
        }

    } catch (e: java.lang.Exception) {
        Log.d(TAG, "+++++++++++++++++++++++++++++++ null array spot 3: $e")
    }
    return cellSig
}

fun getLocalIpAddress(): String? {
    try {
        val en: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
        while (en.hasMoreElements()) {
            val intf: NetworkInterface = en.nextElement()
            val enumIpAddr: Enumeration<InetAddress> = intf.inetAddresses
            while (enumIpAddr.hasMoreElements()) {
                val inetAddress: InetAddress = enumIpAddr.nextElement()
                if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                    val address: String? = inetAddress.getHostAddress()
                    Log.d(TAG, "Local IP address: $address")
                    return address
                }
            }
        }
    } catch (ex: SocketException) {
        ex.printStackTrace()
    }
    return null
}

fun getIPForHostname(hostname: String): String {
    return InetAddress.getByName(hostname).toString().removePrefix("/")
}