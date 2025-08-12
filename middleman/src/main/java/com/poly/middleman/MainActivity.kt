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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.core.os.bundleOf

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Récupérer le package cible
        val targetIntent = intent.getStringExtra("targetIntent") // Par exemple, "com.example.targetapp"
        val uidResult = intent.getStringExtra("uidResult")

        if (targetIntent == null || uidResult == null) {
            val data = Intent().apply {
                bundleOf("data" to "targetIntent est null ? ouuidResult est manquant ?")
            }
            setResult(RESULT_OK, data)
            Log.d("ResultTag_UID:$uidResult", "targetIntent est null ? ouuidResult est manquant ?")
            finish()
        }

        if (targetIntent == "com.poly.intent.middleman") {
            val data = Intent().apply {
                bundleOf("data" to "isOK")
            }
            setResult(RESULT_OK, data)
            Log.d("ResultTag_UID:$uidResult", "middleman v1.0.0 est correctement installé :)")
            finish()
        }

        enableEdgeToEdge()
        setContent {
            CallerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (intent != null)
                        MainScreen(
                            modifier = Modifier.padding(innerPadding),
                            targetIntent = targetIntent!!,
                            uidResult = uidResult!!,
                            initialExtras = intent.extras
                        )
                }
            }
        }
    }

    @Composable
    fun MainScreen(
        modifier: Modifier = Modifier,
        targetIntent: String,
        initialExtras: Bundle?,
        uidResult: String
    ) {
        var resultText by remember { mutableStateOf("En attente du résultat...") }

        val forwardIntentLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
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
            resultText =
                "Code résultat : ${result.resultCode} (${if (result.resultCode == -1) "RESULT_OK" else if (result.resultCode == 0) "RESULT_CANCELED" else "()"})" + extrasMap.entries.joinToString(
                    separator = "\n"
                ) { "${it.key} : ${it.value}" }

            // log result
            Log.d("ResultTag_UID:$uidResult", resultText)
        }

        LaunchedEffect(Unit) {
            createForwardIntent(targetIntent, initialExtras).also { forwardIntent ->
                forwardIntent(forwardIntentLauncher, forwardIntent)
            }
        }

        Column(modifier = modifier) {
            Text("Extras reçus :")
            if (initialExtras == null) {
                Text("Aucun extra reçu")
            } else {
                Text("Intent envoyé (prefix caché)")
                initialExtras.keySet()?.forEach { key ->
                    if (key != "targetIntent" && key != "uidResult") {
                        val value = initialExtras.get(key)
                        Text(
                            "${key.hidePackage()}: ${
                                when (value) {
                                    is String -> value
                                    is Boolean -> value.toString()
                                    is Int -> value.toString()
                                    is Long -> value.toString()
                                    is Float -> value.toString()
                                    is Double -> value.toString()
                                    else -> "Type non supporté: ${value?.javaClass?.simpleName}"
                                }
                            }\n"
                        )
                    }
                }
            }
            Text("Cible : ${targetIntent ?: "Non spécifiée"}")
            Text("---")
            Text("Résultat du deuxième Intent :")
            Text(resultText)
        }
    }


    @Preview(showBackground = true)
    @Composable
    fun MainScreenPreview() {
        CallerTheme {
            MainScreen(targetIntent = "com.example.targetapp", initialExtras = null, uidResult = "UID")
        }
    }
}

fun String.hidePackage(): String {
    return this.split(".").last()
}

fun createForwardIntent(targetIntent: String?, initialExtras: Bundle?): Intent {
    return Intent(targetIntent).apply {
        // Récupérer tous les extras dynamiquement
        initialExtras?.keySet()?.forEach { key ->
            if (key != "targetIntent" && key != "uidResult") {
                val value = initialExtras.get(key)
                when (value) {
                    is String -> putExtra(key, value)
                    is Boolean -> putExtra(key, value)
                    is Int -> putExtra(key, value)
                    is Long -> putExtra(key, value)
                    is Float -> putExtra(key, value)
                    is Double -> putExtra(key, value)
                    else -> println("Type non supporté pour $key: ${value?.javaClass?.simpleName}")
                }
            }
        }
    }
}

fun forwardIntent(forwardIntent: ManagedActivityResultLauncher<Intent, ActivityResult>, secondIntent: Intent) {
    try {
        forwardIntent.launch(secondIntent)
    } catch (e: Exception) {
        Log.e("TAG", "Erreur lors du lancement de l'Intent : ${e.message}")
    }
}