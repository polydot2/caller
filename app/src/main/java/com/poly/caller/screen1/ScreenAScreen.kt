package com.poly.caller.screen1

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.poly.caller.ArrayInput
import com.poly.caller.BooleanInput
import com.poly.caller.EnumInput
import com.poly.caller.NumberInput
import com.poly.caller.TextInput
import com.poly.caller.components.Dropdown
import com.poly.caller.components.SaveAsDialog

@Composable
fun ScreenAScreen(
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("ScreenA")

        if (state.configurationsName.isNotEmpty())
            Dropdown(
                state.config?.name ?: "",
                "",
                options = state.configurationsName,
                onValueChange = { onEvent(ScreenAEvent.LoadAnotherCOnfiguration(it)) }
            )

        if (state.isModified) {
            Row {
                Icon(Icons.Default.Warning, contentDescription = "configuration is modified")
                Button(onClick = { onEvent(ScreenAEvent.Reset) }) { Text("Reset") }
                Button(onClick = { onEvent(ScreenAEvent.ShowSaveAsDialog) }) { Text("Save") }
            }
        }

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

            if (state.showSaveAsDialog)
                SaveAsDialog(
                    title = "Modifier " + config.name,
                    initialValue = config.name,
                    onSave = { newValue ->
                        onEvent(ScreenAEvent.SaveAs(newValue))
                    },
                    onDismiss = {  onEvent(ScreenAEvent.DissmisSaveAsDialog) }
                )
        }
    }
}

//@Composable
//private fun ScreenAScreenPreview() {
//    MaterialTheme {
//        ScreenAScreen(state = ScreenAScreenState(false, null, false, listOf()), onEvent = {})
//    }
//}
