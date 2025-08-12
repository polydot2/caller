package com.poly.annotation

import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.CLASS

/**
 * Example :
 * @Extras(
 *     extras = [
 *         Extra(key = "shouldReadMrz", defaultBoolean = true, type = ExtraType.BOOLEAN, required = false),
 *         Extra(key = "shouldShowReview", defaultBoolean = false, type = ExtraType.BOOLEAN, required = false),
 *         Extra(key = "enabledValidation", defaultString = "dafaultMoi!", type = ExtraType.STRING, required = true),
 *         Extra(key = "listCountries", defaultStringArray = ["fr", "es"], type = ExtraType.STRING_ARRAY, required = true)
 *     ]
 * )
 * @AndroidEntryPoint
 * class TargetActivity : ComponentActivity() {
 */
@Retention(SOURCE)
@Target(CLASS)
annotation class Extras(val extras: Array<Extra>)

annotation class Extra(
    val key: String,
    val type: ExtraType,
    val required: Boolean = false,
    val initMode: ExtraInitMode = ExtraInitMode.NULL,
    val defaultString: String = "",
    val defaultInt: Int = 0,
    val defaultBoolean: Boolean = false,
    val defaultStringArray: Array<String> = [],
)

enum class ExtraType {
    STRING, BOOLEAN, STRING_ARRAY, INT
}

enum class ExtraInitMode {
    NULL, DEFAULT
}

class TypeCastExtraException(message: String) : Exception(message)
class MissingRequiredExtraException(message: String) : Exception(message)