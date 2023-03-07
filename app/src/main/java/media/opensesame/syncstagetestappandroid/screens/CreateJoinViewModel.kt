package media.opensesame.syncstagetestappandroid.screens

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import media.opensesame.syncstagesdk.SyncStage
import media.opensesame.syncstagesdk.SyncStageSDKErrorCode
import javax.inject.Inject

data class LoginUIState(
    val loggedIn: Boolean = false,
    val sessionCode: String = ""
)

@HiltViewModel
class CreateJoinViewModel @Inject constructor(
    private val syncStage: SyncStage
): ViewModel() {
    private val _uiState = MutableStateFlow(LoginUIState())
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()

    private fun updateLoginState(value: Boolean) {
        _uiState.update {
            it.copy(loggedIn = value)
        }
    }

    fun updateSessionCode(code: String) {
        _uiState.update {
            it.copy(
                sessionCode = code
            )
        }
    }

    fun initiateSyncStage() {
        syncStage.init { error ->
            if (error != SyncStageSDKErrorCode.OK) {
                updateLoginState(false)
            } else {
                updateLoginState(true)
            }
        }
    }
}