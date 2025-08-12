package com.poly.caller.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.collections.firstOrNull


class PersistanceRepository {
    val configurations = MutableStateFlow(Configurations.all)

    fun get(): List<Configuration> {
        return configurations.value
    }

    fun save(name: String, configuration: Configuration) {
        val existingConfiguration = Configurations.all.firstOrNull { it.name == name }

        // already exist remove
        existingConfiguration?.let {
            Configurations.all.remove(it)
        }

        // push a new configuration
        Configurations.all.add(configuration.copy())
    }

    fun delete(name: String) {
        val existingConfiguration = Configurations.all.firstOrNull { it.name == name }

        // already exist remove
        existingConfiguration?.let {
            Configurations.all.remove(it)
        }
    }
}

class SpecificRepository(
    val name: String
) {
    init {
        println("SpecificRepository created for module: $name")
    }

    private val configuration = MutableStateFlow(Configurations.all.firstOrNull { it.tag == name && it.name == "default" }?.copy())

    fun get(): Configuration? {
        return configuration.value
    }

    fun save(configurationToSaved: Configuration) {
        configuration.value = configurationToSaved.copy()
    }

    fun observe(): StateFlow<Configuration?> {
        return configuration.asStateFlow()
    }

    fun load(name: String, tag: String) {
        configuration.value = Configurations.all.firstOrNull { it.tag == tag && it.name == name }?.copy()
    }
}

data class Configuration(
    val name: String,
    val tag: String,
    val intentName: String,
    val prefix: String,
    val extras: List<ExtraInput>
)

//
object Configurations {
    val all = mutableListOf(
        Configuration(
            name = "default",
            tag = "module1",
            intentName = "Digitalization",
            prefix = "Digitalization.",
            extras = listOf(
                ExtraInput("label boolean", "techboolean", true),
                ExtraInput("label float", "techfloat", 1f),
                ExtraInput("label int", "techint", 1),
                ExtraInput("label array", "techarray", listOf("es", "fr")),
                ExtraInput("label json", "techjson", "{\"key\"=\"value\"}"),
                ExtraInput("label enum", "techenum", "ONE", listOf("ONE", "TWO")),
            )
        ),
        Configuration(
            name = "default",
            tag = "module2",
            intentName = "SMUL-O2",
            prefix = "SMUL-O2.",
            extras = listOf(
                ExtraInput("label boolean", "techboolean2", true),
                ExtraInput("label float", "techfloat2", 1f),
                ExtraInput("label int", "techint2", 1),
                ExtraInput("label array", "techarray2", listOf("es", "fr")),
                ExtraInput("label json", "techjson2", "{\"key\"=\"value\"}"),
                ExtraInput("label enum", "techenum2", "ONE", listOf("ONE", "TWO")),
            )
        ),
        Configuration(
            name = "azerty",
            tag = "module1",
            intentName = "adad",
            prefix = "ad.",
            extras = listOf(
                ExtraInput("label boolean", "techboolean3", true),
                ExtraInput("label enum", "techenum3", "ONE", listOf("ONE", "TWO")),
            )
        ),
    )
}

data class ExtraInput(
    val label: String,
    val key: String,
    val defaultValue: Any?,
    val options: List<String>? = null,
)