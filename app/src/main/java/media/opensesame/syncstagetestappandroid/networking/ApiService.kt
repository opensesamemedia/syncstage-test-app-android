package media.opensesame.syncstagetestappandroid.networking

import media.opensesame.syncstagetestappandroid.model.GetSyncStageSecretRequest
import media.opensesame.syncstagetestappandroid.model.GetSyncStageSecretResponse
import media.opensesame.syncstagetestappandroid.model.RequestProvisioningRequest
import media.opensesame.syncstagetestappandroid.model.RequestProvisioningResponse
import retrofit2.Call
import retrofit2.http.*


interface ApiService {

    @POST("provisioning/request-provision")
    fun requestProvisioning(
        @Body body: RequestProvisioningRequest
    ): Call<RequestProvisioningResponse>


    @POST("provisioning/request-provision/syncstage-secret")
    fun getSyncStageSecret(
        @Body body: GetSyncStageSecretRequest
    ): Call<GetSyncStageSecretResponse>
}