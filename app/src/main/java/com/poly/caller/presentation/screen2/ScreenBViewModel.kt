package com.poly.caller.presentation.screen2

import com.poly.caller.domain.GetConfigurationsUsecase
import com.poly.caller.domain.InitConfigurationUsecase
import com.poly.caller.domain.LoadConfigurationUsecase
import com.poly.caller.domain.ObserveConfigurationUsecase
import com.poly.caller.domain.SaveConfigurationUsecase
import com.poly.caller.domain.UpdateConfigurationUsecase
import com.poly.caller.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScreenBViewModel @Inject constructor(
    initConfigurationUsecase: InitConfigurationUsecase,
    updateConfigurationUsecase: UpdateConfigurationUsecase,
    observeConfigurationUsecase: ObserveConfigurationUsecase,
    loadConfigurationUsecase: LoadConfigurationUsecase,
    saveConfigurationUsecase: SaveConfigurationUsecase,
    getConfigurationsUsecase: GetConfigurationsUsecase
) : BaseViewModel(initConfigurationUsecase, updateConfigurationUsecase, observeConfigurationUsecase, loadConfigurationUsecase, saveConfigurationUsecase, getConfigurationsUsecase) {

    override val defaultConfigurationName: String = "default"
    override val moduleName: String = "module2"

    init {
        initWith(defaultConfigurationName, moduleName)
    }
}
