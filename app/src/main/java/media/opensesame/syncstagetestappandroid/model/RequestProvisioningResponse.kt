package media.opensesame.syncstagetestappandroid.model

import com.google.gson.annotations.SerializedName

data class RequestProvisioningResponse(

    @field:SerializedName("provisioningCode")
    val provisioningCode: String,

    @field:SerializedName("provisioningCodeId")
    val provisioningCodeId: String
)
