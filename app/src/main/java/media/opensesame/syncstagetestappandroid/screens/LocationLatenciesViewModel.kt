package media.opensesame.syncstagetestappandroid.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import media.opensesame.syncstagesdk.SyncStage
import media.opensesame.syncstagetestappandroid.repo.PreferencesRepo
import java.lang.ref.WeakReference
import javax.inject.Inject


@HiltViewModel
class LocationLatenciesViewModel @Inject constructor(
    private val context: WeakReference<Context>,
    private val syncStage: SyncStage,
    private val preferencesRepo: PreferencesRepo
) : ViewModel() {
    private val _uiState = MutableStateFlow(LocationUIState())
    val uiState: StateFlow<LocationUIState> = _uiState.asStateFlow()
    lateinit var createSessionCallback: (sessionCode: String) -> Unit

//    fun updateSelectedZone(value: Zone) {
//        _uiState.update {
//            it.copy(selectedZone = value)
//        }
//    }
//
//    fun getZones() {
//        CoroutineScope(Dispatchers.IO).launch {
//            val result = syncStage.zoneList()
//            if (result.second == SyncStageSDKErrorCode.OK) {
//                val zones = result.first!!.flatMap { region ->
//                    region.zones.map { zone ->
//                        Zone(zone.zoneId, "${region.regionName} - ${zone.zoneName}")
//                    }
//                }
//                CoroutineScope(Dispatchers.Main).launch {
//                    _uiState.update {
//                        it.copy(zones = zones)
//                    }
//                }
//            } else {
//                CoroutineScope(Dispatchers.Main).launch {
//                    context.get()?.let {
//                        Toast.makeText(
//                            it,
//                            "Failed to get zones - ${result.second}.",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
//                }
//            }
//        }
//    }
//
//    fun createNewSession() {
//        val userId = preferencesRepo.getUserId()
//        CoroutineScope(Dispatchers.IO).launch {
//            val result =
//                syncStage.createSession(_uiState.value.selectedZone.zoneId, userId = userId)
//            if (result.second == SyncStageSDKErrorCode.OK) {
//                result.first?.sessionCode.let { sessionCode ->
//                    CoroutineScope(Dispatchers.Main).launch {
//                        createSessionCallback(sessionCode!!)
//                    }
//                }
//            } else {
//                CoroutineScope(Dispatchers.Main).launch {
//                    context.get()?.let {
//                        Toast.makeText(
//                            it,
//                            "Failed to create new session - ${result.second}.",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
//                }
//            }
//        }
//    }
}