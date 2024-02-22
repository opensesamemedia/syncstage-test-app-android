package media.opensesame.syncstagetestappandroid.screens

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import media.opensesame.syncstagesdk.SyncStage
import media.opensesame.syncstagesdk.SyncStageSDKErrorCode
import media.opensesame.syncstagetestappandroid.repo.PreferencesRepo
import java.lang.ref.WeakReference
import javax.inject.Inject

data class CreateJoinUIState(
    val sessionCode: String = ""
)

@HiltViewModel
class CreateJoinViewModel @Inject constructor(
    private val syncStage: SyncStage,
    private val prefRepo: PreferencesRepo,
    private val context: WeakReference<Context>,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateJoinUIState())
    val uiState: StateFlow<CreateJoinUIState> = _uiState.asStateFlow()
    lateinit var createSessionCallback: (sessionCode: String) -> Unit


    fun updateSessionCode(code: String) {
        _uiState.update {
            it.copy(
                sessionCode = code
            )
        }
    }


    fun createNewSession() {
        val userId = prefRepo.getUserId()
        CoroutineScope(Dispatchers.IO).launch {
            val result =
                syncStage.createSession(
                    userId = userId,
                    zoneId = prefRepo.getZoneId(),
                    studioServerId = prefRepo.getStudioServerId()
                )
            if (result.second == SyncStageSDKErrorCode.OK) {
                result.first?.sessionCode.let { sessionCode ->
                    CoroutineScope(Dispatchers.Main).launch {
                        createSessionCallback(sessionCode!!)
                    }
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    context.get()?.let {
                        Toast.makeText(
                            it,
                            "Failed to create new session - ${result.second}.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
}