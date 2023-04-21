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
import java.lang.RuntimeException
import java.lang.ref.WeakReference
import javax.inject.Inject

data class LoginUIState(
    val loggedIn: Boolean = false,
    val sessionCode: String = ""
)

@HiltViewModel
class CreateJoinViewModel @Inject constructor(
    private val context: WeakReference<Context>,
    private val syncStage: SyncStage
): ViewModel() ***REMOVED***
    private val _uiState = MutableStateFlow(LoginUIState())
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()
    private var initRetries = 0

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
                Log.e("CreateJoinViewModel", "SyncStage init failed")
                updateLoginState(false)

                if (initRetries < 3)***REMOVED***
                    CoroutineScope(Dispatchers.Main).launch ***REMOVED***
                        context.get()?.let ***REMOVED***
                            Toast.makeText(it, "Could not init SyncStage, retrying...", Toast.LENGTH_SHORT).show()
                        ***REMOVED***
                    ***REMOVED***
                    CoroutineScope(Dispatchers.Default).launch ***REMOVED***
                        initRetries += 1
                        delay(2500)
                        initiateSyncStage()
                    ***REMOVED***
                ***REMOVED***else ***REMOVED***
                    CoroutineScope(Dispatchers.Main).launch ***REMOVED***
                        context.get()?.let ***REMOVED***
                            Toast.makeText(it, "Failed to init SyncStage.", Toast.LENGTH_SHORT).show()
                        ***REMOVED***
                    ***REMOVED***
                    throw RuntimeException("SyncStage init retries exceeded.")
                ***REMOVED***

            ***REMOVED*** else ***REMOVED***
                updateLoginState(true)
            ***REMOVED***
        ***REMOVED***
    ***REMOVED***
***REMOVED***