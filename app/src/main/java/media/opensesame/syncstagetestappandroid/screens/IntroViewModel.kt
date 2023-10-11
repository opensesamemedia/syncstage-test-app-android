package media.opensesame.syncstagetestappandroid.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import media.opensesame.syncstagesdk.SyncStage
import media.opensesame.syncstagesdk.SyncStageSDKErrorCode
import media.opensesame.syncstagetestappandroid.BuildConfig
import media.opensesame.syncstagetestappandroid.repo.PreferencesRepo
import java.lang.ref.WeakReference
import javax.inject.Inject

data class LoginUIState(
    val loginInProgress: Boolean = false,
    val loginResult: Boolean? = null,
    val syncStageSecretFileBuiltIn: Boolean? = null,
)

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val context: WeakReference<Context>,
    private val syncStage: SyncStage,
    private val prefRepo: PreferencesRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUIState())
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()
    private var initRetries = 0

    fun getAppVersion(): String {
        return BuildConfig.VERSION_NAME
    }

    private fun updateLoginResult(value: Boolean) {
        _uiState.update {
            it.copy(loginResult = value)
        }
    }


    private fun updateSyncStageSecretBuiltIn(value: Boolean) {
        _uiState.update {
            it.copy(syncStageSecretFileBuiltIn = value)
        }
    }

    private fun updateLoginInProgress(value: Boolean) {
        _uiState.update {
            it.copy(loginInProgress = value)
        }
    }

    fun initiateSyncStage() {
        updateLoginInProgress(true)
        val prefSyncStageSecret = prefRepo.getSyncStageSecret()
        syncStage.init(syncStageSecret = prefSyncStageSecret, onCompleted = { error ->
            updateLoginInProgress(false)
            if (error == SyncStageSDKErrorCode.CONFIGURATION_ERROR) {
                updateSyncStageSecretBuiltIn(false)
                updateLoginResult(false)


            } else if (error != SyncStageSDKErrorCode.OK) {
                Log.e("CreateJoinViewModel", "SyncStage init failed")
                updateSyncStageSecretBuiltIn(true)
                updateLoginResult(false)

                if (initRetries < 3) {
                    CoroutineScope(Dispatchers.Main).launch {
                        context.get()?.let {
                            Toast.makeText(
                                it,
                                "Could not init SyncStage, retrying...",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    CoroutineScope(Dispatchers.Default).launch {
                        initRetries += 1
                        delay(2500)
                        initiateSyncStage()
                    }
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        context.get()?.let {
                            Toast.makeText(it, "Failed to init SyncStage.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            } else {
                updateSyncStageSecretBuiltIn(true)
                updateLoginResult(true)
            }
        })
    }
}