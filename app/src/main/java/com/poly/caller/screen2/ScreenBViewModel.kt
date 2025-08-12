package com.poly.caller.screen2

import androidx.lifecycle.ViewModel
import com.poly.caller.model.GetConfigurationUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ScreenBViewModel @Inject constructor(
    @Named("module1") private val getConfigurationUsecase: GetConfigurationUsecase
) : ViewModel() {
    private val _state = MutableStateFlow(ScreenBScreenState(isLoading = true, null))
    val state = _state.asStateFlow()
    private val _eventChannel = Channel<ScreenBEventFromVM>()
    val events = _eventChannel.receiveAsFlow()

    init {
        _state.value = state.value.copy(false, getConfigurationUsecase())
    }

    fun onEvent(event: ScreenBEvent) {
        when (event) {
            is ScreenBEvent.ExitApplication -> onExitApplication()
        }
    }

    private fun onExitApplication() {
        _eventChannel.trySend(ScreenBEventFromVM.ExitApplication)
    }
}
