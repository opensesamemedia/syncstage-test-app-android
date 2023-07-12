package media.opensesame.syncstagetestappandroid.screens

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


data class LocationUIState(
    val autoSelection: Boolean = true,
)

@HiltViewModel
class LocationViewModel @Inject constructor(
) : ViewModel() {
    private val _uiState = MutableStateFlow(LocationUIState())
    val uiState: StateFlow<LocationUIState> = _uiState.asStateFlow()

    fun updateAutoSelection(value: Boolean) {
        _uiState.update {
            it.copy(autoSelection = value)
        }
    }
}