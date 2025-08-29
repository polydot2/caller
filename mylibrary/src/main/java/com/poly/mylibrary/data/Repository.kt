package com.poly.mylibrary.data

import androidx.lifecycle.ViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class Configuration(val param1: String, val param2: Boolean)
data class Result(val success: Boolean, val data: String)

interface ConfigurationRepository {
    fun save(config: Configuration)
    val configuration: Configuration?
}

class ConfigurationRepositoryImpl @Inject constructor() : ConfigurationRepository {
    private var _configuration: Configuration? = null
    override fun save(config: Configuration) {
        _configuration = config
    }

    override val configuration: Configuration? get() = _configuration
}

interface ResultRepository {
    val resultFlow: StateFlow<Result?>
    fun setResult(result: Result?)
}

class ResultRepositoryImpl @Inject constructor() : ResultRepository {
    private val _result = MutableStateFlow<Result?>(null)
    override val resultFlow: StateFlow<Result?> = _result.asStateFlow()
    override fun setResult(result: Result?) {
        _result.value = result
    }
}

@Module
@InstallIn(SingletonComponent::class)
object MyLibModule {
    @Provides
    fun provideConfigurationRepository(): ConfigurationRepository {
        return ConfigurationRepositoryImpl()
    }

    @Provides
    fun provideResultRepository(): ResultRepository {
        return ResultRepositoryImpl()
    }
}

@HiltViewModel
class CaptureScreenViewModel @Inject constructor(
    val configurationRepository: ConfigurationRepository,
    val resultRepository: ResultRepository
) : ViewModel() {
    suspend fun someCallSuspend(): String {
        // Simule l'appel asynchrone (remplace par ton implémentation)
        return "Processed data"
    }
}
