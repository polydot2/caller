package com.poly.caller.screen1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poly.caller.model.GetConfigurationUsecase
import com.poly.caller.model.LoadConfigurationUsecase
import com.poly.caller.model.ObserveConfigurationUsecase
import com.poly.caller.model.PersistanceRepository
import com.poly.caller.model.SaveConfigurationUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ScreenAViewModel @Inject constructor(
    @Named("module1") private val moduleName: String,
    @Named("module1") private val getConfigurationUsecase: GetConfigurationUsecase,
    @Named("module1") private val saveConfigurationUsecase: SaveConfigurationUsecase,
    @Named("module1") private val observeConfigurationUsecase: ObserveConfigurationUsecase,
    @Named("module1") private val loadConfigurationUsecase: LoadConfigurationUsecase,
    private val persistance: PersistanceRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ScreenAScreenState(isLoading = true, null, false, listOf(), false))
    val state = _state.asStateFlow()
    private val _eventChannel = Channel<ScreenAEventFromVM>()
    val events = _eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            observeConfigurationUsecase().collect {
                // get original values
                val original = persistance.get().first { config ->
                    config.tag == it?.tag && config.name == it.name
                }

                // compare
                var isModified = it != original

                // All configuration from tag
                val allConfigFamily = persistance.get().filter { config ->
                    config.tag == moduleName
                }.map { it.name }

                // update UI
                _state.value = state.value.copy(false, it, isModified, allConfigFamily)
            }
        }
    }

    fun onEvent(event: ScreenAEvent) {
        when (event) {
            is ScreenAEvent.ExitApplication -> onExitApplication()
            is ScreenAEvent.UpdateExtraInput -> updateExtraInput(event.key, event.value)
            is ScreenAEvent.LoadAnotherCOnfiguration -> loadConfiguration(event.configurationToLoad)
            is ScreenAEvent.Reset -> reset()
            is ScreenAEvent.ShowSaveAsDialog -> showSaveAsDialog()
            is ScreenAEvent.DissmisSaveAsDialog -> dismissSaveAsDialog()
            is ScreenAEvent.SaveAs -> saveConfigurationInPersistance(event.name)
        }
    }


    private fun reset() {
        _state.value.config?.let {
            loadConfigurationUsecase(it.name, it.tag)
        }
    }

    private fun saveConfigurationInPersistance(name: String) {
        viewModelScope.launch {
            // TODO if name == "default" cannot be modified
            if (name != "default") {
                val currentConfig = _state.value.config ?: return@launch
                persistance.save(name, currentConfig)
                loadConfiguration(name)
            }
        }
    }

    private fun showSaveAsDialog() {
        _state.value = state.value.copy(showSaveAsDialog = true)
    }

    private fun dismissSaveAsDialog() {
        _state.value = state.value.copy(showSaveAsDialog = false)
    }

    private fun loadConfiguration(configurationToLoad: String) {
        loadConfigurationUsecase(configurationToLoad, moduleName)
    }

    private fun updateExtraInput(key: String, newValue: Any) {
        viewModelScope.launch {
            val currentConfig = _state.value.config ?: return@launch

            val updatedExtras = currentConfig.extras.map { extraInput ->
                if (extraInput.key == key) {
                    extraInput.copy(defaultValue = newValue)
                } else {
                    extraInput
                }
            }
            val updatedConfig = currentConfig.copy(extras = updatedExtras)

            // Mettre à jour la configuration
            saveConfigurationUsecase(updatedConfig)
        }
    }

    private fun onExitApplication() {
        _eventChannel.trySend(ScreenAEventFromVM.ExitApplication)
    }
}
