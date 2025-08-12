package com.poly.target

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.poly.annotation.Extra
import com.poly.annotation.ExtraInitMode.*
import com.poly.annotation.ExtraType.*
import com.poly.annotation.Extras
import com.poly.annotation.MissingRequiredExtraException
import com.poly.annotation.TypeCastExtraException
import com.poly.target.TargetActivityExtrasConfig
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@Extras(
    [
        Extra("shouldReadMrz", BOOLEAN, false, NULL),
        Extra("minAge", INT, false, NULL, defaultInt = 10),
        Extra("shouldShowReview", BOOLEAN, false, NULL, defaultBoolean = false),
        Extra("enabledValidation", STRING, false, NULL, defaultString = "default"),
        Extra("listCountries", STRING_ARRAY, false, NULL, defaultStringArray = ["AR", "FR"]),
        Extra("listCountries2", STRING_ARRAY, false, NULL, defaultStringArray = ["es"]),
        Extra("shouldReadMrz3", BOOLEAN, false, DEFAULT),
        Extra("minAge3", INT, false, DEFAULT, defaultInt = 10),
        Extra("shouldShowReview3", BOOLEAN, false, DEFAULT, defaultBoolean = false),
        Extra("enabledValidation3", STRING, false, DEFAULT, defaultString = "default"),
        Extra("listCountries3", STRING_ARRAY, false, DEFAULT, defaultStringArray = ["es"]),
        Extra("listCountries4", STRING_ARRAY, false, DEFAULT, defaultStringArray = []),
        Extra("TYPE", STRING, false, DEFAULT, defaultString = "TYPE.NONE"),
    ]
)
@AndroidEntryPoint
class TargetActivity : ComponentActivity(
//    val viewModel : TargetViewModel = hiltViewmodel()
) {
    @Inject
    lateinit var extrasConfig: TargetActivityExtrasConfig

//    var res = ResultModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            extrasConfig.initialize(intent)
        } catch (e: MissingRequiredExtraException) {
            val data = Intent().apply {
                putExtra("data", "ERROR_MISSING_01")
            }
            setResult(RESULT_CANCELED, data)
            finish()
        } catch (e: TypeCastExtraException) {
            val data = Intent().apply {
                putExtra("data", "ERROR_BAD_CASTING_01")
            }
            setResult(RESULT_CANCELED, data)
            finish()
        }

        setContent {
            MaterialTheme {
                Column {
                    Text("TARGET ! Welcome !")

                    Button(onClick = {
                        val json = Json.encodeToString("RESULTMODEL ici! :)")
                        setResult(RESULT_OK, Intent().putExtra("data", json))
                        finish()
                    }) {
                        Text("Valider")
                    }
                    Text("intent : \n" + intent.extras.toString())
                    Spacer(Modifier.height(12.dp))
                    Text("config : \n" + extrasConfig.toMap().entries.joinToString("\n") { it.key + " = " + it.value.toString() })
                }
            }
        }
    }
}