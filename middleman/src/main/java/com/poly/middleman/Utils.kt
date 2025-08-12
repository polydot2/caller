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

    if (targetIntent == null || uidResult == null) {
        val data = Intent().apply {
            bundleOf("data" to "targetIntent est null ?")
        }
        activity.setResult(RESULT_OK, data)
        Log.d("ResultTag_UID:$uidResult", "targetIntent est null ?")
        activity.finish()
    }

    if (targetIntent == "com.poly.intent.middleman") {
        val data = Intent().apply {
            bundleOf("data" to data)
        }
        activity.setResult(RESULT_OK, data)
        Log.d("ResultTag_UID:$uidResult", "middleman v1.0.0 est correctement installé :)")
        activity.finish()
    }

    return targetIntent!! to uidResult!!
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

fun createForwardIntent(targetIntent: String, initialIntent: Intent): Intent {
    return initialIntent.apply {
        action = targetIntent
        removeExtra("targetIntent")
        removeExtra("uidResult")
    }
}

fun forwardIntent(forwardIntent: ManagedActivityResultLauncher<Intent, ActivityResult>, secondIntent: Intent) {
    try {
        forwardIntent.launch(secondIntent)
    } catch (e: Exception) {
        Log.e("TAG", "Erreur lors du lancement de l'Intent : ${e.message}")
    }
}

fun getValueToString(value: Any): String {
    return when (value) {
        is String -> value
        is Boolean -> value.toString()
        is Int -> value.toString()
        is Long -> value.toString()
        is Float -> value.toString()
        is Double -> value.toString()
        else -> "Type non supporté: ${value.javaClass.simpleName}"
    }
}
