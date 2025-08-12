package com.poly.caller.screen2

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.poly.caller.ArrayInput
import com.poly.caller.BooleanInput
import com.poly.caller.EnumInput
import com.poly.caller.NumberInput
import com.poly.caller.TextInput
import com.poly.caller.screen1.ScreenAEvent
import com.poly.caller.screen1.ScreenAEventFromVM
import com.poly.caller.screen1.ScreenAScreenState
import com.poly.caller.screen1.ScreenAViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ScreenBScreen(
    viewModel: ScreenAViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest {
            when (it) {
                is ScreenAEventFromVM.ExitApplication -> {
                }
            }
        }
    }

    ScreenAScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun ScreenAScreen(
    state: ScreenAScreenState,
    onEvent: (ScreenAEvent) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("ScreenBBB")
        state.config?.let { config ->
            Text(config.intentName)

            LazyColumn {
                items(config.extras) { item ->
                    when (val defaultValue = item.defaultValue) {
                        is Boolean -> BooleanInput(
                            label = item.label,
                            value = defaultValue,
                            onValueChange = { newValue ->
                                onEvent(ScreenAEvent.UpdateExtraInput(item.key, newValue))
                            }
                        )
                        is Int -> NumberInput(
                            label = item.label,
                            value = defaultValue.toString(),
                            keyboardType = KeyboardType.Number,
                            onValueChange = { newValue ->
                                newValue.toIntOrNull()?.let {
                                    onEvent(ScreenAEvent.UpdateExtraInput(item.key, it))
                                }
                            }
                        )
                        is Float -> NumberInput(
                            label = item.label,
                            value = defaultValue.toString(),
                            keyboardType = KeyboardType.Decimal,
                            onValueChange = { newValue ->
                                newValue.toFloatOrNull()?.let {
                                    onEvent(ScreenAEvent.UpdateExtraInput(item.key, it))
                                }
                            }
                        )
                        is List<*> -> ArrayInput(
                            label = item.label,
                            value = defaultValue as List<String>,
                            onValueChange = { newValue ->
                                onEvent(ScreenAEvent.UpdateExtraInput(item.key, newValue))
                            }
                        )
                        is String -> {
                            if (item.options != null) {
                                EnumInput(
                                    label = item.label,
                                    value = defaultValue,
                                    options = item.options,
                                    onValueChange = { newValue ->
                                        onEvent(ScreenAEvent.UpdateExtraInput(item.key, newValue))
                                    }
                                )
                            } else {
                                TextInput(
                                    label = item.label,
                                    value = defaultValue,
                                    onValueChange = { newValue ->
                                        onEvent(ScreenAEvent.UpdateExtraInput(item.key, newValue))
                                    }
                                )
                            }
                        }
                        null -> Text("No value for ${item.label}")
                        else -> Text("Unsupported type for ${item.label}: ${defaultValue::class.simpleName}")
                    }
                }
            }
        }
    }
}
