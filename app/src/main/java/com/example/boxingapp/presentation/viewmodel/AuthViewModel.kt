package com.example.boxingapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boxingapp.data.preferences.UserPreferenceRepository
import com.example.boxingapp.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(
    private val userRepository: UserRepository,
    private val prefs: UserPreferenceRepository
) : ViewModel() {

    val loggedInUser: StateFlow<String?> = prefs.loggedInUserFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val success = userRepository.login(username, password)
            if (success) {
                prefs.setLoggedInUser(username)
                _error.value = null
            } else {
                _error.value = "Neispravni podaci"
            }
        }
    }

    fun register(username: String, password: String) {
        viewModelScope.launch {
            val success = userRepository.register(username, password)
            if (success) {
                prefs.setLoggedInUser(username)
                _error.value = null
            } else {
                _error.value = "Korisnik već postoji"
            }
        }
    }

    fun logout() {
        viewModelScope.launch { prefs.setLoggedInUser(null) }
    }
}
