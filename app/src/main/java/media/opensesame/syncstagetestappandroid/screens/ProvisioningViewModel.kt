package media.opensesame.syncstagetestappandroid.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import media.opensesame.syncstagesdk.SyncStage
import media.opensesame.syncstagesdk.SyncStageSDKErrorCode
import media.opensesame.syncstagetestappandroid.data.ProvisioningUIState
import media.opensesame.syncstagetestappandroid.model.GetSyncStageSecretRequest
import media.opensesame.syncstagetestappandroid.model.GetSyncStageSecretResponse
import media.opensesame.syncstagetestappandroid.model.RequestProvisioningRequest
import media.opensesame.syncstagetestappandroid.model.RequestProvisioningResponse
import media.opensesame.syncstagetestappandroid.networking.ApiConfig
import media.opensesame.syncstagetestappandroid.repo.PreferencesRepo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltViewModel
class ProvisioningViewModel @Inject constructor(
    private val context: WeakReference<Context>,
    private val syncStage: SyncStage,
    private val prefRepo: PreferencesRepo
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProvisioningUIState(provisioningCode = ""))
    val uiState: StateFlow<ProvisioningUIState> = _uiState.asStateFlow()

    private var pollingJob: Job? = null
    private val pollingIntervalMillis = 2000L // 2 seconds
    private val timeoutMillis = 300000L // 5 minutes

    lateinit var onSyncStageProvisioned: () -> Unit


    fun updateProvisioningInProgress(inProgress: Boolean){
        _uiState.update {
        it.copy(provisioningInProgress = inProgress)
    }}

    fun updateProvisioningCode(provisioningCode: String){
        _uiState.update {
            it.copy(provisioningCode = provisioningCode)
    }}

    fun stopPolling() {
        pollingJob?.cancel()
    }
    fun startPollingForSyncStageSecret(getSyncStageSecretRequest: GetSyncStageSecretRequest) {
        pollingJob = GlobalScope.launch(Dispatchers.IO) {
            val startTime = System.currentTimeMillis()


            while (System.currentTimeMillis() - startTime < timeoutMillis) {
                val client = ApiConfig.getApiService().getSyncStageSecret(
                    getSyncStageSecretRequest
                )

                client.enqueue(object : Callback<GetSyncStageSecretResponse> {

                    override fun onResponse(
                        call: Call<GetSyncStageSecretResponse>,
                        response: Response<GetSyncStageSecretResponse>
                    ) {
                        val responseBodyString = Gson().toJson(response.body())
                        if (response.isSuccessful) {

                            displayToast("Application provisioned.")
                            updateProvisioningInProgress(false)
                            syncStage.init(syncStageSecret = responseBodyString, onCompleted = { errorCode: SyncStageSDKErrorCode ->
                                if (errorCode == SyncStageSDKErrorCode.OK){
                                    onSyncStageProvisioned()
                                } else {
                                    displayToast("SyncStage Secret invalid.")
                                }
                            })
                            prefRepo.updateSyncStageSecret(responseBodyString)
                            stopPolling()
                            return
                        }
                    }

                    override fun onFailure(call: Call<GetSyncStageSecretResponse>, t: Throwable) {
                        t.message?.let { displayToast(it) }
                        t.printStackTrace()
                    }
                })

                delay(pollingIntervalMillis)
            }
            displayToast("Application not provisioned due to a timeout waiting for organization acceptance.")
            updateProvisioningInProgress(false)
        }
    }


    fun sendProvisioningCode() {
        _uiState.update {
            it.copy(provisioningInProgress = true)
        }

        val client = ApiConfig.getApiService().requestProvisioning(
            RequestProvisioningRequest(
                provisioningCode = uiState.value.provisioningCode,
                operatingSystem = "Android",
                ipAddress = "TODO",
                sdkVersion = syncStage.getSDKVersion(),
                model = "TODO"
            )
        )
        client.enqueue(object : Callback<RequestProvisioningResponse> {

            override fun onResponse(
                call: Call<RequestProvisioningResponse>,
                response: Response<RequestProvisioningResponse>
            ) {
                val responseBody = response.body()
                if (!response.isSuccessful) {
                    displayToast("Unknown provisioning code.")
                    _uiState.update {
                        it.copy(provisioningInProgress = false)
                    }
                    return
                } else {
                    displayToast("Provisioning code valid. Waiting for organization acceptance.")
                    startPollingForSyncStageSecret(GetSyncStageSecretRequest(provisioningCode=responseBody?.provisioningCode, provisioningCodeId = responseBody?.provisioningCodeId))
                }
            }

            override fun onFailure(call: Call<RequestProvisioningResponse>, t: Throwable) {
                t.message?.let { displayToast(it) }
                t.printStackTrace()
            }
        })
    }
    private fun displayToast(inputMessage: String) {
        CoroutineScope(Dispatchers.Main).launch {
            context.get()?.let {
                Toast.makeText(
                    it,
                    inputMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}

