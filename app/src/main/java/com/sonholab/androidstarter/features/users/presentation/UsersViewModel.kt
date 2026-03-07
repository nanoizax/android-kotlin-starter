package com.sonholab.androidstarter.features.users.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonholab.androidstarter.features.users.domain.model.UserSummary
import com.sonholab.androidstarter.features.users.domain.repository.UsersRepository
import com.sonholab.androidstarter.features.users.domain.usecase.GetUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// -------------------------------------------------------------------------
// UI State
// -------------------------------------------------------------------------

sealed class UsersUiState {
    data object Loading : UsersUiState()
    data class Content(
        val users: List<UserSummary>,
        val isRefreshing: Boolean = false,
    ) : UsersUiState()
    data class Error(val message: String) : UsersUiState()
}

// -------------------------------------------------------------------------
// ViewModel
// -------------------------------------------------------------------------

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val usersRepository: UsersRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UsersUiState>(UsersUiState.Loading)
    val uiState: StateFlow<UsersUiState> = _uiState.asStateFlow()

    init {
        observeUsers()
        refresh()
    }

    /**
     * Observes the local Room cache. New emissions arrive automatically whenever
     * [refresh] writes fresh data to the database.
     */
    private fun observeUsers() {
        getUsersUseCase()
            .onEach { users ->
                _uiState.update { current ->
                    val refreshing = (current as? UsersUiState.Content)?.isRefreshing ?: false
                    UsersUiState.Content(users = users, isRefreshing = refreshing)
                }
            }
            .catch { throwable ->
                _uiState.value = UsersUiState.Error(
                    throwable.message ?: "Failed to load users.",
                )
            }
            .launchIn(viewModelScope)
    }

    /**
     * Triggers a network refresh. Marks the state as refreshing while in-flight.
     */
    fun refresh() {
        viewModelScope.launch {
            // Mark as refreshing if we already have content to display.
            _uiState.update { current ->
                if (current is UsersUiState.Content) {
                    current.copy(isRefreshing = true)
                } else {
                    UsersUiState.Loading
                }
            }

            try {
                usersRepository.refresh()
            } catch (e: Exception) {
                // If refresh fails but we already have cached content, keep showing it
                // and display the error as a transient message via Error state.
                _uiState.update { current ->
                    if (current is UsersUiState.Content) {
                        current.copy(isRefreshing = false)
                    } else {
                        UsersUiState.Error(e.message ?: "Failed to refresh users.")
                    }
                }
            }
        }
    }
}
