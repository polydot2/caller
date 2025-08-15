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
        // Supprimer toutes les configurations existantes avec le même name et tag
        Configurations.all.removeAll { it.name == name && it.tag == configuration.tag }

        // Ajouter la nouvelle configuration avec le name spécifié
        Configurations.all.add(configuration.copy(name = name))
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

    private val configuration = MutableStateFlow(Configurations.all.firstOrNull { it.tag == name && it.name == "default" })

    fun get(): Configuration? {
        return configuration.value
    }

    fun update(configurationToSaved: Configuration) {
        configuration.value = configurationToSaved
    }

    fun observe(): StateFlow<Configuration?> {
        return configuration.asStateFlow()
    }

    fun load(name: String, tag: String) {
        configuration.value = Configurations.all.firstOrNull { it.tag == tag && it.name == name }
    }
}

object Configurations {
    val all = mutableListOf(
        Configuration(
            name = "default",
            tag = "module1",
            intentName = "Digitalization",
            prefix = "Digitalization.",
            extras = listOf(
                ExtraInput.BooleanInput("label boolean", "techboolean", true),
                ExtraInput.FloatInput("label float", "techfloat", 1f),
                ExtraInput.IntInput("label int", "techint", 1),
                ExtraInput.ListStringInput("label array", "techarray", listOf("es", "fr")),
                ExtraInput.StringInput("label json", "techjson", "{\"key\":\"value\"}"),
                ExtraInput.StringInput("label enum", "techenum", "ONE", listOf("ONE", "TWO")),
            )
        ),
        Configuration(
            name = "CheckID variant1",
            tag = "module1",
            intentName = "adad",
            prefix = "ad.",
            extras = listOf(
                ExtraInput.BooleanInput("label boolean", "techboolean3", true),
                ExtraInput.StringInput("label enum", "techenum3", "ONE", listOf("ONE", "TWO")),
            )
        ),
        Configuration(
            name = "default",
            tag = "module2",
            intentName = "SMUL-O2",
            prefix = "SMUL-O2.",
            extras = listOf(
                ExtraInput.BooleanInput("label boolean", "techboolean2", true),
                ExtraInput.FloatInput("label float", "techfloat2", 1f),
                ExtraInput.IntInput("label int", "techint2", 1),
                ExtraInput.ListStringInput("label array", "techarray2", listOf("es", "fr")),
                ExtraInput.StringInput("label json", "techjson2", "{\"key\":\"value\"}"),
                ExtraInput.StringInput("label enum", "techenum2", "ONE", listOf("ONE", "TWO")),
            )
        ),
        Configuration(
            name = "TaxReturn Shortcut",
            tag = "module2",
            intentName = "adad",
            prefix = "ad.",
            extras = listOf(
                ExtraInput.BooleanInput("label boolean", "techboolean3", true),
                ExtraInput.StringInput("label enum", "techenum3", "ONE", listOf("ONE", "TWO")),
            )
        ),
    )
}

data class Configuration(
    val name: String,
    val tag: String,
    val intentName: String,
    val prefix: String,
    val extras: List<ExtraInput>,
)

sealed class ExtraInput(
    open val label: String,
    open val key: String,
) {
    data class IntInput(
        override val label: String,
        override val key: String,
        val defaultValue: Int,
    ) : ExtraInput(label, key)

    data class FloatInput(
        override val label: String,
        override val key: String,
        val defaultValue: Float,
    ) : ExtraInput(label, key)

    data class StringInput(
        override val label: String,
        override val key: String,
        val defaultValue: String,
        val options: List<String>? = null
    ) : ExtraInput(label, key)

    data class BooleanInput(
        override val label: String,
        override val key: String,
        val defaultValue: Boolean,
    ) : ExtraInput(label, key)

    data class ListStringInput(
        override val label: String,
        override val key: String,
        val defaultValue: List<String>,
    ) : ExtraInput(label, key)
}
