package com.poly.mylibrary

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.poly.mylibrary.data.Result
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CameraScreen(
    viewModel: CameraViewModel = hiltViewModel(),
    exitApplication: (shouldShowPopup: Boolean) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest {
            when (it) {
                is CameraEventFromVM.ExitApplication -> {
                    exitApplication(false)
                }
            }
        }
    }

    CameraScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun CameraScreen(
    state: CameraScreenState,
    onEvent: (CameraEvent) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("ok")
    }
}

@Composable
private fun CameraScreenPreview() {
    MaterialTheme {
        CameraScreen(state = CameraScreenState(false), onEvent = {})
    }
}
