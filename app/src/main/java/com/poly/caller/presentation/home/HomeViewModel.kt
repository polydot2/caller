package com.poly.caller.presentation.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
) : ViewModel() {
    private val _state = MutableStateFlow(HomeScreenState(isLoading = true))
    val state = _state.asStateFlow()
    private val _eventChannel = Channel<HomeEventFromVM>()
    val events = _eventChannel.receiveAsFlow()

    /**
     * Méthode permettant de gérer les événements en provenance de la couche UI
     * @param event Evenement en provenance de la couche UI
     */
    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.NavigateTo -> onNavigate(event.index)
        }
    }

    private fun onNavigate(index: Int) {
        _eventChannel.trySend(HomeEventFromVM.NavigateTo(index))
    }
}
