package com.poly.caller.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun TextInput(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { newText ->
            onValueChange(newText)
        },
        label = { Text(label) }
    )
}

@Composable
fun NumberInput(label: String, value: String, keyboardType: KeyboardType, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { newText ->
            onValueChange(newText)
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}

@Composable
fun BooleanInput(label: String, value: Boolean, onValueChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label)
        Checkbox(
            checked = value,
            onCheckedChange = { newChecked ->
                onValueChange(newChecked)
            },
            modifier = Modifier
        )
    }
}

@Composable
fun ArrayInput(label: String, value: List<String>, onValueChange: (List<String>) -> Unit) {
    Text("$label: ${value.joinToString(", ")}")
    Button(onClick = { onValueChange(value + "new") }) {
        Text("Add item to list")
    }
}

@Composable
fun EnumInput(label: String, value: String, options: List<String>, onValueChange: (String) -> Unit) {
    Dropdown(
        label,
        value,
        options = options,
        onValueChange = { newChecked ->
            onValueChange(newChecked)
        }
    )
}