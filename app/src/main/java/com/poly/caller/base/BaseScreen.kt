package com.poly.caller.base

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.poly.caller.ArrayInput
import com.poly.caller.BooleanInput
import com.poly.caller.EnumInput
import com.poly.caller.NumberInput
import com.poly.caller.TextInput
import com.poly.caller.components.Dropdown
import com.poly.caller.components.SaveAsDialog
import com.poly.caller.model.ExtraInput
import kotlinx.coroutines.flow.collectLatest

@Composable
fun BaseScreen(
    viewModel: BaseViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest {
            when (it) {
                is BaseEventFromVM.ExitApplication -> {
                }
            }
        }
    }

    BaseScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun BaseScreen(
    state: BaseScreenState,
    onEvent: (BaseEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (state.isModified)
                        Icon(Icons.Rounded.Warning, contentDescription = "configuration is modified", modifier = Modifier.padding(bottom = 4.dp, end = 8.dp))
                    Text(if (state.isModified) "Parcours modifié" else "Parcours")
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { onEvent(BaseEvent.Reset) }, enabled = state.isModified) { Icon(Icons.Rounded.Refresh, contentDescription = "reset configuration") }
                    Button(onClick = { onEvent(BaseEvent.ShowSaveAsDialog) }, enabled = state.isModified) { Text("Save") }
                }

                if (state.configurationsName.isNotEmpty())
                    Dropdown(
                        state.config?.name ?: "",
                        state.config?.name ?: "",
                        options = state.configurationsName,
                        onValueChange = { onEvent(BaseEvent.LoadAnotherCOnfiguration(it)) }
                    )
            }
        }

        Spacer(Modifier.height(16.dp))

        state.config?.let { config ->
            LazyColumn {
                items(config.extras) { item ->
                    when (item) {
                        is ExtraInput.BooleanInput -> BooleanInput(
                            label = item.label,
                            value = item.defaultValue,
                            onValueChange = { newValue ->
                                onEvent(BaseEvent.UpdateExtraInput(item.key, ExtraInput.BooleanInput(item.label, item.key, newValue)))
                            }
                        )

                        is ExtraInput.IntInput -> NumberInput(
                            label = item.label,
                            value = item.defaultValue.toString(),
                            keyboardType = KeyboardType.Number,
                            onValueChange = { newValue ->
                                newValue.toIntOrNull()?.let {
                                    onEvent(BaseEvent.UpdateExtraInput(item.key, ExtraInput.IntInput(item.label, item.key, it)))
                                }
                            }
                        )

                        is ExtraInput.FloatInput -> NumberInput(
                            label = item.label,
                            value = item.defaultValue.toString(),
                            keyboardType = KeyboardType.Decimal,
                            onValueChange = { newValue ->
                                newValue.toFloatOrNull()?.let {
                                    onEvent(BaseEvent.UpdateExtraInput(item.key, ExtraInput.FloatInput(item.label, item.key, it)))
                                }
                            }
                        )

                        is ExtraInput.ListStringInput -> ArrayInput(
                            label = item.label,
                            value = item.defaultValue,
                            onValueChange = { newValue ->
                                onEvent(BaseEvent.UpdateExtraInput(item.key, ExtraInput.ListStringInput(item.label, item.key, newValue)))
                            }
                        )

                        is ExtraInput.StringInput -> {
                            if (item.options != null) {
                                EnumInput(
                                    label = item.label,
                                    value = item.defaultValue,
                                    options = item.options,
                                    onValueChange = { newValue ->
                                        onEvent(BaseEvent.UpdateExtraInput(item.key, ExtraInput.StringInput(item.label, item.key, newValue, item.options)))
                                    }
                                )
                            } else {
                                TextInput(
                                    label = item.label,
                                    value = item.defaultValue,
                                    onValueChange = { newValue ->
                                        onEvent(BaseEvent.UpdateExtraInput(item.key, ExtraInput.StringInput(item.label, item.key, newValue, item.options)))
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (state.showSaveAsDialog)
                SaveAsDialog(
                    title = "Modifier " + config.name,
                    initialValue = config.name,
                    onSave = { newValue ->
                        onEvent(BaseEvent.SaveAs(newValue))
                    },
                    onDismiss = { onEvent(BaseEvent.DissmisSaveAsDialog) }
                )
        }
    }
}