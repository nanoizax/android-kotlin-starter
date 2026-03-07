package com.sonholab.androidstarter.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonholab.androidstarter.core.domain.Result
import com.sonholab.androidstarter.features.auth.domain.model.User
import com.sonholab.androidstarter.features.auth.domain.usecase.LoginUseCase
import com.sonholab.androidstarter.features.auth.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// -------------------------------------------------------------------------
// UI State
// -------------------------------------------------------------------------

sealed class LoginUiState {
    data object Idle : LoginUiState()
    data object Loading : LoginUiState()
    data class Success(val user: User) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

// -------------------------------------------------------------------------
// ViewModel
// -------------------------------------------------------------------------

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (_uiState.value is LoginUiState.Loading) return

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            when (val result = loginUseCase(email, password)) {
                is Result.Success -> _uiState.value = LoginUiState.Success(result.data)
                is Result.Error   -> _uiState.value = LoginUiState.Error(
                    result.exception.message ?: "An unexpected error occurred.",
                )
                is Result.Loading -> Unit // Use case never emits Loading; handled by Idle state.
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _uiState.value = LoginUiState.Idle
        }
    }

    fun clearError() {
        if (_uiState.value is LoginUiState.Error) {
            _uiState.value = LoginUiState.Idle
        }
    }
}
