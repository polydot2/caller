package com.poly.middleman

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.core.os.bundleOf

fun handleIntent(activity: Activity, intent: Intent): Pair<String, String> {
    val targetIntent = intent.getStringExtra("targetIntent") // Par exemple, "com.example.targetapp"
    val uidResult = intent.getStringExtra("uidResult")

    if (targetIntent == null) {
        val data = Intent().apply {
            putExtras(
                bundleOf("data" to "targetIntent est null ?")
            )
        }
        activity.setResult(RESULT_OK, data)
        foreachExtras(data) { key, value -> Log.d("ResultTag_UID:$uidResult", "$key:$value") }
        activity.finish()
    }

    if (uidResult == null) {
        val data = Intent().apply {
            putExtras(
                bundleOf("data" to "uidResult est null ?")
            )
        }
        activity.setResult(RESULT_OK, data)
        foreachExtras(data) { key, value -> Log.d("ResultTag_UID:$uidResult", "$key:$value") }
        activity.finish()
    }

    if (targetIntent == "com.poly.intent.middleman") {
        Log.d("ResultTag_UID:$uidResult", "La cible est soi même \uD83E\uDD17")
        val data = Intent().apply {
            putExtras(
                bundleOf(
                    "status" to "middleman est correctement installé :)",
                    "version" to "v1.0.0",
                    "a bientot" to "\uD83D\uDC4B"
                )
            )
        }
        activity.setResult(RESULT_OK, data)
        foreachExtras(data) { key, value -> Log.d("ResultTag_UID:$uidResult", "$key:$value") }
        activity.finish()
    }

    return Pair(targetIntent!!, uidResult!!)
}

fun String.hidePackage(): String {
    return this.split(".").last()
}

fun String.getPrefix(): String {
    val list = this.split(".")
    return if (list.count() == 1)
        ""
    else
        list.subList(0, list.size - 1).joinToString(".")
}

fun createForwardIntent(targetIntent: String, initialIntent: Intent): Intent? {
    return try {
        val forwardIntent = Intent(targetIntent).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            initialIntent.extras?.keySet()?.forEach { key ->
                if (key != "targetIntent" && key != "uidResult") {
                    putExtra(key, initialIntent.extras?.get(key)?.toString())
                }
            }
        }
        Log.d("TAGTAG", "Forward Intent créé avec action: $targetIntent")
        forwardIntent
    } catch (e: Exception) {
        Log.e("TAGTAG", "Erreur lors de la création de l'intent cible: ${e.message}")
        null
    }
}

fun forwardIntent(launcher: ManagedActivityResultLauncher<Intent, ActivityResult>, intent: Intent) {
    try {
        Log.d("TAGTAG", "Lancement de l'Intent avec action: ${intent.action}")
        launcher.launch(intent)
    } catch (e: Exception) {
        Log.e("TAGTAG", "Erreur lors du lancement de l'Intent: ${e.message}")
    }
}

fun visualizeExtras(intent: Intent): Map<String, String>? {
    return intent.extras?.let { bundle ->
        buildMap {
            bundle.keySet().forEach { key ->
                // Exclure targetIntent et uidResult si nécessaire
                if (key != "targetIntent" && key != "uidResult") {
                    val value = bundle.get(key)
                    // Convertir la valeur en String de manière sécurisée
                    put(key, value?.toString() ?: "null")
                }
            }
        }
    }
}

fun foreachExtras(intent: Intent, callback: (String, String) -> Unit) {
    intent.extras?.let { bundle ->
        bundle.keySet().forEach { key ->
            val value = bundle.get(key)?.toString() ?: "null"
            callback(key, value)
        }
    }
}