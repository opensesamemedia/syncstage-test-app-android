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
): ViewModel() ***REMOVED***
    private val _uiState = MutableStateFlow(LoginUIState())
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()

    private fun updateLoginState(value: Boolean) ***REMOVED***
        _uiState.update ***REMOVED***
            it.copy(loggedIn = value)
        ***REMOVED***
    ***REMOVED***

    fun updateSessionCode(code: String) ***REMOVED***
        _uiState.update ***REMOVED***
            it.copy(
                sessionCode = code
            )
        ***REMOVED***
    ***REMOVED***

    fun initiateSyncStage() ***REMOVED***
        syncStage.init ***REMOVED*** error ->
            if (error != SyncStageSDKErrorCode.OK) ***REMOVED***
                updateLoginState(false)
            ***REMOVED*** else ***REMOVED***
                updateLoginState(true)
            ***REMOVED***
        ***REMOVED***
    ***REMOVED***
***REMOVED***