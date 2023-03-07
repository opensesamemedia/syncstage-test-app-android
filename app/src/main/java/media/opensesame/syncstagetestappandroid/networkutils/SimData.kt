package media.opensesame.networkutils

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.util.Log

data class SimData(
    var dualSim: Boolean = false,
    val carrierNameSim1: String,
    val mccSim1: String,
    val mncSim1: String,
    var carrierNameSim2: String? = null,
    var mccSim2: String? = null,
    var mncSim2: String? = null,
) ***REMOVED***
    fun getCarrierString(): String ***REMOVED***
        var info: String = "$carrierNameSim1 $mccSim1$mncSim1"
        if (dualSim) ***REMOVED***
            info += " | $carrierNameSim2 $mccSim2$mncSim2"
        ***REMOVED***
        return info
    ***REMOVED***
***REMOVED***

@SuppressLint("MissingPermission")
fun getSimData(context: Context): SimData ***REMOVED***
    //for dual sim mobile
    //for dual sim mobile
    val localSubscriptionManager: SubscriptionManager = SubscriptionManager.from(context)
    val localList: List<*> = localSubscriptionManager.activeSubscriptionInfoList
    var simData: SimData =
        try ***REMOVED***
            val simInfo1: SubscriptionInfo = localList[0] as SubscriptionInfo
            SimData(
                carrierNameSim1 = simInfo1.carrierName.toString(),
                mccSim1 = simInfo1.mccString.toString(),
                mncSim1 = simInfo1.mncString.toString()
            )
        ***REMOVED*** catch (e: IndexOutOfBoundsException) ***REMOVED***
            Log.w("getSimData", "Fake sim card, no sim data found on the phone")

            SimData(
                carrierNameSim1 = "no sim",
                mccSim1 = "",
                mncSim1 = ""
            )
        ***REMOVED***


    if (localSubscriptionManager.activeSubscriptionInfoCount > 1) ***REMOVED***

        val simInfo2: SubscriptionInfo = localList[1] as SubscriptionInfo
        simData.dualSim = true
        simData.carrierNameSim2 = simInfo2.carrierName.toString()
        simData.mccSim2 = simInfo2.mccString.toString()
        simData.mncSim2 = simInfo2.mncString.toString()
    ***REMOVED***
    return simData
***REMOVED***