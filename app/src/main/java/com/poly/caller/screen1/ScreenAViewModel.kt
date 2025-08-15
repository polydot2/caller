package com.poly.caller.screen1

import com.poly.caller.base.BaseViewModel
import com.poly.caller.model.GetConfigurationsUsecase
import com.poly.caller.model.InitConfigurationUsecase
import com.poly.caller.model.LoadConfigurationUsecase
import com.poly.caller.model.ObserveConfigurationUsecase
import com.poly.caller.model.SaveConfigurationUsecase
import com.poly.caller.model.UpdateConfigurationUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScreenAViewModel @Inject constructor(
    initConfigurationUsecase: InitConfigurationUsecase,
    updateConfigurationUsecase: UpdateConfigurationUsecase,
    observeConfigurationUsecase: ObserveConfigurationUsecase,
    loadConfigurationUsecase: LoadConfigurationUsecase,
    saveConfigurationUsecase: SaveConfigurationUsecase,
    getConfigurationsUsecase: GetConfigurationsUsecase
) : BaseViewModel(initConfigurationUsecase, updateConfigurationUsecase, observeConfigurationUsecase, loadConfigurationUsecase, saveConfigurationUsecase, getConfigurationsUsecase) {

    override val defaultConfigurationName: String = "default"
    override val moduleName: String = "module1"

    init {
        initWith(defaultConfigurationName, moduleName)
    }
}
