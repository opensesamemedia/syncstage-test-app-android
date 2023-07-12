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
import java.lang.ref.WeakReference
import javax.inject.Inject

data class LoginUIState(
    val loggedIn: Boolean = false,
)

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val context: WeakReference<Context>,
    private val syncStage: SyncStage
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUIState())
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()
    private var initRetries = 0

    fun getAppVersion(): String {
        return BuildConfig.VERSION_NAME
    }

    private fun updateLoginState(value: Boolean) {
        _uiState.update {
            it.copy(loggedIn = value)
        }
    }

    fun initiateSyncStage() {
        syncStage.init { error ->
            if (error != SyncStageSDKErrorCode.OK) {
                Log.e("CreateJoinViewModel", "SyncStage init failed")
                updateLoginState(false)

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
                updateLoginState(true)
            }
        }
    }
}