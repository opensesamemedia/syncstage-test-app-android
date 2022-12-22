package com.example.syncstagetestappandroid.screens

import androidx.lifecycle.ViewModel
import com.example.syncstagetestappandroid.data.ProfileUIState
import com.example.syncstagetestappandroid.repo.PreferencesRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val prefRepo: PreferencesRepo
): ViewModel() ***REMOVED***
    private val _uiState = MutableStateFlow(ProfileUIState(userName = prefRepo.getUserName()))
    val uiState: StateFlow<ProfileUIState> = _uiState.asStateFlow()

    fun updateUserName(userName: String) ***REMOVED***
        _uiState.update ***REMOVED***
            it.copy(userName = userName)
        ***REMOVED***
        prefRepo.updateUserName(userName)
    ***REMOVED***

    fun createUserId() ***REMOVED***
        val userId = prefRepo.getUserId()
        if (userId.isEmpty()) ***REMOVED***
            val uuid = UUID.randomUUID().toString()
            prefRepo.updateUserId(uuid)
        ***REMOVED***
    ***REMOVED***
***REMOVED***