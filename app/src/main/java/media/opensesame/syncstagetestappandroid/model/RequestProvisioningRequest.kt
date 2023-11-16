package media.opensesame.syncstagetestappandroid.model

import com.google.gson.annotations.SerializedName

data class RequestProvisioningRequest(

    @field:SerializedName("provisioningCode")
    val provisioningCode: String? = null,

    @field:SerializedName("ipAddress")
    val ipAddress: String? = null,

    @field:SerializedName("model")
    val model: String? = null,

    @field:SerializedName("sdkVersion")
    val sdkVersion: String? = null,

    @field:SerializedName("operatingSystem")
    val operatingSystem: String? = null
)
