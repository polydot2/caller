package com.poly.caller.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poly.caller.SpecificRepositoryFactory
import com.poly.caller.model.Configuration
import com.poly.caller.model.ExtraInput
import com.poly.caller.model.GetConfigurationUsecase
import com.poly.caller.model.LoadConfigurationUsecase
import com.poly.caller.model.ObserveConfigurationUsecase
import com.poly.caller.model.PersistanceRepository
import com.poly.caller.model.SaveConfigurationUsecase
import com.poly.caller.model.SpecificRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel(
    private val repositoryFactory: SpecificRepositoryFactory,
    private val persistance: PersistanceRepository
) : ViewModel() {

    abstract val moduleName: String

    private val specificRepository: SpecificRepository by lazy { repositoryFactory.create(moduleName) }
    private val getConfigurationUsecase: GetConfigurationUsecase by lazy { GetConfigurationUsecase(specificRepository) }
    private val saveConfigurationUsecase: SaveConfigurationUsecase by lazy { SaveConfigurationUsecase(specificRepository) }
    private val observeConfigurationUsecase: ObserveConfigurationUsecase by lazy { ObserveConfigurationUsecase(specificRepository) }
    private val loadConfigurationUsecase: LoadConfigurationUsecase by lazy { LoadConfigurationUsecase(specificRepository) }

    private val _state = MutableStateFlow(BaseScreenState(isLoading = true, null, false, listOf(), false))
    val state = _state.asStateFlow()
    private val _eventChannel = Channel<BaseEventFromVM>()
    val events = _eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            observeConfigurationUsecase().collect {
                updateView(it)
            }
        }
    }

    fun onEvent(event: BaseEvent) {
        when (event) {
            is BaseEvent.ExitApplication -> onExitApplication()
            is BaseEvent.UpdateExtraInput -> updateExtraInput(event.key, event.value)
            is BaseEvent.LoadAnotherCOnfiguration -> loadConfiguration(event.configurationToLoad)
            is BaseEvent.Reset -> reset()
            is BaseEvent.ShowSaveAsDialog -> showSaveAsDialog()
            is BaseEvent.DissmisSaveAsDialog -> dismissSaveAsDialog()
            is BaseEvent.SaveAs -> saveAs(event.name)
        }
    }

    private fun saveAs(name: String) {
        viewModelScope.launch {
            val currentConfig = _state.value.config ?: return@launch

            // save
            persistance.save(name, currentConfig)

            // reload useless.. current config is already updated
            loadConfiguration(name)

            // updateView
            updateView(currentConfig)
        }
    }

    fun updateView(it: Configuration?) {
        // get original values
        val original = persistance.get().first { config ->
            config.tag == it?.tag && config.name == it.name
        }

        // compare
        var isModified = it != original

        // all configuration from tag
        val allConfigFamily = persistance.get().filter { config ->
            config.tag == moduleName
        }.map { it.name }

        // update UI
        _state.value = state.value.copy(false, it, isModified, allConfigFamily)
    }


    private fun reset() {
        _state.value.config?.let {
            loadConfigurationUsecase(it.name, it.tag)
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

    private fun updateExtraInput(key: String, newValue: ExtraInput) {
        viewModelScope.launch {
            val currentConfig = _state.value.config ?: return@launch

            val updatedExtras = currentConfig.extras.map { extraInput ->
                if (extraInput.key == key) {
                    newValue
                } else {
                    extraInput
                }
            }
            val updatedConfig = currentConfig.copy(
                extras = updatedExtras,
            )

            // Mettre à jour la configuration
            saveConfigurationUsecase(updatedConfig)
        }
    }

    private fun onExitApplication() {
        _eventChannel.trySend(BaseEventFromVM.ExitApplication)
    }
}
