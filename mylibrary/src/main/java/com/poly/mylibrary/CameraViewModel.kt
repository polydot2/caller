package com.poly.mylibrary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poly.mylibrary.data.ConfigurationRepository
import com.poly.mylibrary.data.Result
import com.poly.mylibrary.data.ResultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class CameraViewModel @Inject constructor(
    val configurationRepository: ConfigurationRepository,
    val resultRepository: ResultRepository
) : ViewModel() {
    private val _state = MutableStateFlow(CameraScreenState(isLoading = true))
    val state = _state.asStateFlow()
    private val _eventChannel = Channel<CameraEventFromVM>()
    val events = _eventChannel.receiveAsFlow()

    /**
     * Méthode permettant de gérer les événements en provenance de la couche UI
     * @param event Evenement en provenance de la couche UI
     */
    fun onEvent(event: CameraEvent) {
        when (event) {
            is CameraEvent.ExitApplication -> onExitApplication()
        }
    }

    private fun onExitApplication() {
        _eventChannel.trySend(CameraEventFromVM.ExitApplication)

        viewModelScope.launch {
            try {
                val result = verifyDdoc()

                // vérification ici !!
                // trop bien !

                resultRepository.setResult(result)
            } catch (e: Exception) {
                resultRepository.setResult(Result(success = false, data = "Error: ${e.message}"))
            }
        }
    }
}

suspend fun verifyDdoc(): Result = suspendCoroutine<Result> { continuation ->
    val manager = Manager()
    manager.call(object : Listener() {
        override fun onSuccess(result: Result) {
            print(result.data)
            continuation.resume(result)
        }

        override fun onError(error: String) {
            print(error)
            continuation.resumeWithException(error.toException())
        }
    })
}

private fun String.toException(): Throwable {
    return Exception(this)
}

class Manager() {
    fun call(listener: Listener) {
        // make some call !!
    }
}

abstract class Listener() {
    abstract fun onSuccess(result: Result)
    abstract fun onError(error: String)
}
