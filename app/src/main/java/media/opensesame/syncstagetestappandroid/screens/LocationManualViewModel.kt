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

data class ServerInstance(
    val zoneId: String,
    val zoneName: String,
    val studioServerId: String
)

data class LocationManualUIState(
    val selectedServerInstance: ServerInstance = ServerInstance("", "", ""),
    val serverInstances: List<ServerInstance> = listOf(),
)

@HiltViewModel
class LocationManualViewModel @Inject constructor(
    private val context: WeakReference<Context>,
    private val syncStage: SyncStage,
    private val preferencesRepo: PreferencesRepo
) : ViewModel() {
    private val _uiState = MutableStateFlow(LocationManualUIState())
    val uiState: StateFlow<LocationManualUIState> = _uiState.asStateFlow()


    fun updateSelectedServer(value: ServerInstance) {
        _uiState.update {
            it.copy(selectedServerInstance = value)
        }
        preferencesRepo.updateStudioServerId(studioServerId = value.studioServerId)
        preferencesRepo.updateZoneId(zoneId = value.zoneId)
    }

    fun getServerInstances() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = syncStage.getServerInstances()
            if (result.second == SyncStageSDKErrorCode.OK) {
                CoroutineScope(Dispatchers.Main).launch {
                    _uiState.update { it ->
                        it.copy(serverInstances = result.first!!.map {
                            // this is a workaround for not being able to use ServerInstace dataclass definition from the sdk
                                server ->
                            ServerInstance(
                                zoneId = server.zoneId,
                                zoneName = server.zoneName,
                                studioServerId = server.studioServerId
                            )
                        })
                    }
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    context.get()?.let {
                        Toast.makeText(
                            it,
                            "Failed to get server instances - ${result.second}.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

}