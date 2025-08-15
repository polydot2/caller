package com.poly.caller.screen1

import com.poly.caller.SpecificRepositoryFactory
import com.poly.caller.base.BaseViewModel
import com.poly.caller.model.PersistanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScreenAViewModel @Inject constructor(
    repositoryFactory: SpecificRepositoryFactory,
    persistance: PersistanceRepository
) : BaseViewModel(repositoryFactory, persistance){
    override val moduleName: String
        get() = "module1"
}
