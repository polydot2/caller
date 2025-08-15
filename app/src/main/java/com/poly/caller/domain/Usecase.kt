package com.poly.caller.domain

import com.poly.caller.model.Configuration
import com.poly.caller.model.PersistanceRepository
import com.poly.caller.model.SpecificRepository
import kotlinx.coroutines.flow.StateFlow

class UpdateConfigurationUsecase(
    private val repository: SpecificRepository
) {
    operator fun invoke(configuration: Configuration) {
        repository.update(configuration)
    }
}

class ObserveConfigurationUsecase(
    private val repository: SpecificRepository
) {
    operator fun invoke(): StateFlow<Configuration?> {
        return repository.observe()
    }
}

class LoadConfigurationUsecase(
    private val repository: SpecificRepository
) {
    operator fun invoke(name: String) {
        return repository.load(name)
    }
}

class InitConfigurationUsecase(
    private val repository: SpecificRepository
) {
    operator fun invoke(name: String, tag: String) {
        return repository.init(name, tag)
    }
}

class SaveConfigurationUsecase(
    private val repository: PersistanceRepository
) {
    operator fun invoke(name: String, configuration: Configuration) {
        return repository.save(name, configuration)
    }
}

class GetConfigurationsUsecase(
    private val repository: PersistanceRepository
) {
    operator fun invoke(): List<Configuration> {
        return repository.getAll()
    }
}