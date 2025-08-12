package com.poly.middleman

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.poly.middleman.ui.theme.CallerTheme

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import com.poly.middleman.components.ExpandableSection

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Récupérer le package cible et uidResult
        val pair = handleIntent(this, intent) // missing

        enableEdgeToEdge()
        setContent {
            CallerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (intent != null)
                        MainScreen(
                            modifier = Modifier.padding(innerPadding),
                            targetIntent = pair.first,
                            uidResult = pair.second,
                            initialIntent = intent,
                            extrasMapString = visualizeExtras(intent),
                        )
                }
            }
        }
    }

    private fun visualizeExtras(intent: Intent): Map<String, String>? {
        return intent.extras?.keySet()?.let {
            val map: Map<String, String> = mutableMapOf()
            it.forEach { key ->
                if (key != "targetIntent" && key != "uidResult") {
                    map.apply {
                        key to getValueToString(intent.extras?.get(key)!!)
                    }
                }
            }
            map
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    targetIntent: String,
    uidResult: String,
    initialIntent: Intent,
    extrasMapString: Map<String, String>?
) {
    var resultText by remember { mutableStateOf("En attente du résultat...") }
    var showLoader: Boolean = true

    val forwardIntentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        resultText = handleResult(result, uidResult) // handle result
    }

    LaunchedEffect(Unit) {
        // launch target intent
        createForwardIntent(targetIntent, initialIntent).also { forwardIntent ->
            forwardIntent(forwardIntentLauncher, forwardIntent)
        }
    }

    LaunchedEffect(resultText) {
        if (resultText == "En attente du résultat...")
            showLoader = true
        else
            showLoader = false
    }

    // Reporting
//    var scroll by rememberScrollState()
    Column(modifier = modifier.padding(16.dp)) {
        Text("Cible : $targetIntent")

        extrasMapString?.let {
            ExpandableSection("Extras to forward : ${it.size}") {
                for (entrie in extrasMapString.entries) {
                    Text(entrie.key + ":" + entrie.value)
                }
            }
        } ?: run {
            Text("Aucun extra reçu")
        }

        Text("Résultat du deuxième Intent : ")

        if (showLoader) {
            CircularProgressIndicator()
        }

        Text(resultText)
    }
}

private fun handleResult(result: ActivityResult, uidResult: String): String {
    val data = result.data
    val extrasMap = mutableMapOf<String, String>()

    // Extraire les extras du résultat
    data?.extras?.let {
        it.keySet()?.forEach { key ->
            val value = it.get(key)
            extrasMap[key] = when (value) {
                is String -> value
                is Boolean -> value.toString()
                is Int -> value.toString()
                is Long -> value.toString()
                is Float -> value.toString()
                is Double -> value.toString()
                else -> "Type non supporté: ${value?.javaClass?.simpleName}"
            }
        }
    }

    // show result on middle app
    val resultText =
        "Code résultat : ${result.resultCode} (${if (result.resultCode == -1) "RESULT_OK" else if (result.resultCode == 0) "RESULT_CANCELED" else "()"})" + extrasMap.entries.joinToString(
            separator = "\n"
        ) { "${it.key} : ${it.value}" }

    // log result
    Log.d("ResultTag_UID:$uidResult", resultText)
    return resultText
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    CallerTheme {
        MainScreen(targetIntent = "com.example.targetapp", uidResult = "UID", initialIntent = Intent(), extrasMapString = mapOf("key" to "value", "key" to "value", "key" to "value"))
    }
}
