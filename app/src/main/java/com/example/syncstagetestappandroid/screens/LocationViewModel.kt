package com.example.syncstagetestappandroid.screens

import androidx.lifecycle.ViewModel
import com.example.syncstagetestappandroid.repo.PreferencesRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import media.opensesame.syncstagesdk.SyncStage
import javax.inject.Inject

data class Zone(
    val zoneId: String = "",
    val ZoneName: String = ""
)

data class ZonesUIState(
    val zones: List<Zone> = listOf(),
    val selectedZone: Zone = Zone("","")
)

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val syncStage: SyncStage,
    private val preferencesRepo: PreferencesRepo
): ViewModel() ***REMOVED***
    private val _uiState = MutableStateFlow(ZonesUIState())
    val uiState: StateFlow<ZonesUIState> = _uiState.asStateFlow()
    lateinit var createSessionCallback: (sessionCode: String) -> Unit

    fun updateSelectedZone(value: Zone) ***REMOVED***
        _uiState.update ***REMOVED***
            it.copy(selectedZone = value)
        ***REMOVED***
    ***REMOVED***

    fun getZones() ***REMOVED***
        CoroutineScope(Dispatchers.IO).launch ***REMOVED***
            val result = syncStage.zonesList()
            if (!result.first.isNullOrEmpty()) ***REMOVED***
                val zones = result.first!!.flatMap ***REMOVED*** region ->
                    region.zones.map ***REMOVED*** zone ->
                        Zone(zone.zoneId, "$***REMOVED***region.regionName***REMOVED*** - $***REMOVED***zone.zoneName***REMOVED***")
                    ***REMOVED***
                ***REMOVED***
                CoroutineScope(Dispatchers.Main).launch ***REMOVED***
                    _uiState.update ***REMOVED***
                        it.copy(zones = zones)
                    ***REMOVED***
                ***REMOVED***
            ***REMOVED***
        ***REMOVED***
    ***REMOVED***

    fun createNewSession() ***REMOVED***
        val userId = preferencesRepo.getUserId()
        CoroutineScope(Dispatchers.IO).launch ***REMOVED***
            val result = syncStage.createSession(_uiState.value.selectedZone.zoneId, userId = userId)
            result.first?.sessionCode.let ***REMOVED*** sessionCode ->
                CoroutineScope(Dispatchers.Main).launch ***REMOVED***
                    createSessionCallback(sessionCode!!)
                ***REMOVED***
            ***REMOVED***
        ***REMOVED***
    ***REMOVED***
***REMOVED***