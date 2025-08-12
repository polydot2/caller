package com.poly.target.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import dagger.hilt.android.AndroidEntryPoint
import com.poly.`annotation`.ExtraData.*
import com.poly.annotation.ExtraInitMode.*

@AndroidEntryPoint
class ATTTtActivity : ComponentActivity() {
//    val extras = listOf(
//        BooleanExtra("shouldReadMrz", false, DEFAULT, true),
//        IntExtra("minAge", false, DEFAULT, 12),
//        StringExtra("enabledValidation", false, DEFAULT, "default"),
//        StringArrayExtra("listCountries3", false, DEFAULT, listOf<String>("es"))
//    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
//            extrasConfig2.initialize(intent)

            setContent {
                MaterialTheme {
                    Button(onClick = {
//                        val resultModel = ResultModel.Builder()
//                            .shouldReadMrz(extrasConfig.getExtraTyped("shouldReadMrz") ?: true)
//                            .shouldShowReview(extrasConfig.getExtraTyped("shouldShowReview") ?: false)
//                            .enabledValidation(extrasConfig.getExtraTyped("enabledValidation") ?: true)
//                            .listCountries(extrasConfig.getExtraTyped<Array<String>>("listCountries")?.toList() ?: listOf("fr", "es"))
//                            .additionalData("some_value")
//                            .build()
//                        val json = Json.encodeToString(resultModel)
//                        setResult(RESULT_OK, Intent().putExtra("result", json))
//                        finish()
                    }) {
                        Text("Valider")
                    }
                }
            }
        } catch (e: IllegalArgumentException) {
            Log.e("TargetActivity", e.message ?: "Erreur")
        }
    }
}