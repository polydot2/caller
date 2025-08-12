package com.poly.caller

import android.R.attr.text
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import kotlin.collections.plus

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
    Checkbox(
        checked = value,
        onCheckedChange = { newChecked ->
            onValueChange(newChecked)
        },
        modifier = Modifier
    )
    Text(label)
}

@Composable
fun ArrayInput(label: String, value: List<String>, onValueChange: (List<String>) -> Unit) {
    // Exemple simple : affiche la liste, mais pourrait être un composant plus complexe
    Text("$label: ${value.joinToString(", ")}")
    // Pour modifier la liste, vous pourriez ajouter un composant interactif
    // Pour l'exemple, on simule une modification statique
    Button(onClick = { onValueChange(value + "new") }) {
        Text("Add item to list")
    }
}

@Composable
fun EnumInput(label: String, value: String, options: List<String>, onValueChange: (String) -> Unit) {
    var selected by remember { mutableStateOf(value) }
    var expanded by remember { mutableStateOf(false) }
//    DropdownMenuBox(
//        expanded = expanded,
//        onExpandedChange = { expanded = !expanded }
//    ) {
//        OutlinedTextField(
//            value = selected,
//            onValueChange = {},
//            label = { Text(label) },
//            readOnly = true,
//            modifier = Modifier.menuAnchor()
//        )
//        DropdownMenu(
//            expanded = expanded,
//            onDismissRequest = { expanded = false }
//        ) {
//            options.forEach { option ->
//                DropdownMenuItem(
//                    text = { Text(option) },
//                    onClick = {
//                        selected = option
//                        onValueChange(option)
//                        expanded = false
//                    }
//                )
//            }
//        }
//    }
    Button(onClick = { onValueChange(value + "new") }) {
        Text("Add item to list")
    }
}