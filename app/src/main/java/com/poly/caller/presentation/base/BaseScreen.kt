package com.poly.caller.presentation.base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.poly.caller.model.ExtraInput
import com.poly.caller.presentation.components.ArrayInput
import com.poly.caller.presentation.components.BooleanInput
import com.poly.caller.presentation.components.Dropdown
import com.poly.caller.presentation.components.EnumInput
import com.poly.caller.presentation.components.NumberInput
import com.poly.caller.presentation.components.SaveAsDialog
import com.poly.caller.presentation.components.TextInput
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
    val lazyListState = rememberLazyListState() // État de la LazyColumn
    val isScrolled by remember { derivedStateOf { lazyListState.firstVisibleItemIndex > 0 || lazyListState.firstVisibleItemScrollOffset > 0 } }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Card(Modifier.padding(horizontal = 16.dp)) {
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
                Column {
                    if (isScrolled) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)) // Ligne
                        )
                    }
                }

                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 80.dp),
                ) {
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
            }
        }

        Button(
            onClick = { /* Action du bouton */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Text("Launch intent")
        }

        if (state.showSaveAsDialog) {
            SaveAsDialog(
                title = "Modifier " + (state.config?.name ?: ""),
                initialValue = state.config?.name ?: "",
                onSave = { newValue ->
                    onEvent(BaseEvent.SaveAs(newValue))
                },
                onDismiss = { onEvent(BaseEvent.DissmisSaveAsDialog) }
            )
        }
    }
}