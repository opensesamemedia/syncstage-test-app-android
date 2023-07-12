package media.opensesame.syncstagetestappandroid.screens

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import media.opensesame.syncstagetestappandroid.data.ProfileUIState
import media.opensesame.syncstagetestappandroid.repo.PreferencesRepo
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val prefRepo: PreferencesRepo
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUIState(userName = prefRepo.getUserName()))
    val uiState: StateFlow<ProfileUIState> = _uiState.asStateFlow()

    fun updateUserName(userName: String) {
        _uiState.update {
            it.copy(userName = userName)
        }
        prefRepo.updateUserName(userName)
    }

    fun createUserId() {
        val userId = prefRepo.getUserId()
        if (userId.isEmpty()) {
            val uuid = UUID.randomUUID().toString()
            prefRepo.updateUserId(uuid)
        }
    }
}