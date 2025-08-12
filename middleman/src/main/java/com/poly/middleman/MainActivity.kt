package com.poly.middleman

import android.R.attr.data
import android.app.Activity.RESULT_OK
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import com.poly.middleman.components.ExpandableSection

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pair = handleIntent(this, intent) // missing

        enableEdgeToEdge()
        setContent {
            CallerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
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
    var showLoader by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()

    val forwardIntentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("TAGTAG", "Callback du launcher déclenché avec resultCode: ${result.resultCode}, data: ${result.data}")
        resultText = handleResult(result, uidResult)
        showLoader = false
    }

    // Créer et lance l'intent cible
    LaunchedEffect(Unit) {
        val forwardIntent = createForwardIntent(targetIntent, initialIntent)
        if (forwardIntent != null) {
            forwardIntent(forwardIntentLauncher, forwardIntent)
        } else {
            resultText = "Erreur : Impossible de créer l'intent cible"
            showLoader = false
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
    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text("UID : $uidResult")
        Text("Intent cible : $targetIntent")
        Spacer(Modifier.height(16.dp))

        extrasMapString?.let {
            ExpandableSection("Extras pour l'intent cible : ${it.size}") {
                for (entrie in extrasMapString.entries) {
                    Text(entrie.key + ":" + entrie.value)
                }
            }
        } ?: run {
            Text("Aucun extra reçu")
        }

        Spacer(Modifier.height(16.dp))

        Text("Résultat : ")

        Spacer(Modifier.height(16.dp))

        if (showLoader) {
            CircularProgressIndicator()
        }

        Spacer(Modifier.height(16.dp))

        Text(resultText)
    }
}

private fun handleResult(result: ActivityResult, uidResult: String): String {
    val codeResult = "Code résultat : ${result.resultCode} (${
        when (result.resultCode) {
            -1 -> "RESULT_OK"
            0 -> "RESULT_CANCELED"
            else -> "Unknown"
        }
    })"

    // Construire la chaîne de résultat
    val resultText = buildString {
        append(codeResult)
        result.data?.let { intent ->
            val extrasList = mutableListOf<String>()
            foreachExtras(intent) { key, value ->
                extrasList.add("$key:$value")
                // Logger chaque extra
                // Log.d("ResultTag_UID:$uidResult", "$key:$value")
            }
            if (extrasList.isNotEmpty()) {
                append("\n")
                append(extrasList.joinToString("\n"))
            } else {
                append("\nAucun extra dans le résultat")
            }
        } ?: append("\nAucun extra dans le résultat")
    }

    // Logger le résultat complet
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
