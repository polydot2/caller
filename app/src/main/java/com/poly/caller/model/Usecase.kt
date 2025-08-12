package com.poly.caller.model

import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetConfigurationUsecase @Inject constructor(
    private val repository: SpecificRepository
) {
    operator fun invoke(): Pair<String, Configuration?> {
        return repository.name to repository.get()
    }
}

class SaveConfigurationUsecase(
    private val repository: SpecificRepository
) {
    operator fun invoke(configuration: Configuration) {
        repository.save(configuration)
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
    operator fun invoke(name: String, tag: String) {
        return repository.load(name, tag)
    }
}