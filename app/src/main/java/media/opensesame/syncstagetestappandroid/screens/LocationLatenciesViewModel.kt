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
import media.opensesame.syncstagesdk.delegates.SyncStageDiscoveryDelegate
import media.opensesame.syncstagetestappandroid.repo.PreferencesRepo
import java.lang.ref.WeakReference
import javax.inject.Inject

data class ZoneLatency(
    val name: String,
    val latency: Int
)

data class LocationLatenciesUIState(
    val results: List<ZoneLatency> = listOf(),
    val zones: List<String> = listOf(),
    val selectedStudioInstance: ServerInstance? = null
)

@HiltViewModel
class LocationLatenciesViewModel @Inject constructor(
    private val context: WeakReference<Context>,
    private val syncStage: SyncStage,
    private val preferencesRepo: PreferencesRepo
) : ViewModel(), SyncStageDiscoveryDelegate {
    private val _uiState = MutableStateFlow(LocationLatenciesUIState())
    val uiState: StateFlow<LocationLatenciesUIState> = _uiState.asStateFlow()

    init {
        syncStage.discoveryDelegate = this
    }


    fun getServerInstances() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = syncStage.getBestAvailableServer()
            if (result.second == SyncStageSDKErrorCode.OK) {
                preferencesRepo.updateZoneId(result.first!!.zoneId)
                preferencesRepo.updateStudioServerId(result.first!!.studioServerId)
                _uiState.update {
                    it.copy(
                        selectedStudioInstance = ServerInstance(
                            zoneId = result.first!!.zoneId,
                            zoneName = result.first!!.zoneName,
                            studioServerId = result.first!!.studioServerId
                        )
                    )
                }

            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    context.get()?.let {
                        Toast.makeText(
                            it,
                            "Failed to get zones - ${result.second}.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    override fun discoveryLatencyTestResults(zoneLatencyMap: Map<String, Int>) {
        _uiState.update {
            it.copy(results = zoneLatencyMap.entries.map { (name, latency) ->
                ZoneLatency(name, latency)
            })
        }
    }

    override fun discoveryResults(zones: List<String>) {
        _uiState.update {
            it.copy(zones = zones)
        }
    }
}