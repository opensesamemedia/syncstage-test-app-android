package media.opensesame.syncstagetestappandroid.model

import com.google.gson.annotations.SerializedName

data class GetSyncStageSecretRequest(

    @field:SerializedName("provisioningCode")
    val provisioningCode: String? = null,

    @field:SerializedName("provisioningCodeId")
    val provisioningCodeId: String? = null
)
