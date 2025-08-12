package com.poly.ksp

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.poly.annotation.ExtraData
import com.poly.annotation.ExtraData.*
import com.poly.annotation.ExtraInitMode
import com.poly.annotation.ExtraType
import com.poly.annotation.ExtraType.*
import com.poly.annotation.Extras
import generateIntentBuilder
import kotlin.collections.toList

class ExtrasProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("Démarrage d'ExtrasProcessor")

        val symbols = resolver.getSymbolsWithAnnotation(Extras::class.qualifiedName!!)

        logger.info("Processing round with ${symbols.count()} symbols")

        val invalidSymbols = mutableListOf<KSAnnotated>()

        // Pour chaque @Extras, on extrait une map avec les paramètres key, type, required, et les valeurs par défauts
        symbols.filter { it is KSClassDeclaration }.forEach { symbol ->

            val currentClass = symbol as KSClassDeclaration

            logger.info("Traitement de la classe : ${currentClass.javaClass.simpleName}")

            // extract extras from @Extras(extra = [..])
            val extras = retrievesExtras2(currentClass, symbol)

            logger.info("extras: " + extras.toList().joinToString("\n\n"))

            // file names
            val className = currentClass.simpleName.asString()
            val packageName = currentClass.packageName.asString()

            // create config file
            val fileNameConfig = "${className}ExtrasConfig"
            codeGenerator.createNewFile(
                dependencies = Dependencies(false, symbol.containingFile!!),
                packageName = packageName,
                fileName = fileNameConfig
            ).use { output -> output.write(generateExtrasConfigClass(packageName, fileNameConfig, extras).toByteArray()) }

            // create builder file
            val fileNameBuilder = "${className}IntentBUilder"
            codeGenerator.createNewFile(
                dependencies = Dependencies(false, symbol.containingFile!!),
                packageName = packageName,
                fileName = fileNameBuilder
            ).use { output -> output.write(generateIntentBuilder(packageName, fileNameBuilder, extras).toByteArray()) }
        }

        // Retourner les symboles invalides
        logger.warn("Invalid symbol : ${invalidSymbols.count()} ")
        return invalidSymbols
    }

    private fun retrievesExtras2(currentClass: KSClassDeclaration, symbol: KSClassDeclaration): List<ExtraData> {
        val listExtras = mutableListOf<ExtraData>()

        currentClass.annotations
            .filter { it.shortName.asString() == "Extras" } // @Extras
            .forEach { annotation ->
                // liste des extras en paramètre de @Extras
                val extrasArray = annotation.arguments.find { it.name?.asString() == "extras" }?.value as? List<KSAnnotation> ?: emptyList() // extras=[..]
                extrasArray.mapIndexed { index, extraAnnotation ->

                    // valeurs par défaut, n'est jamais null car une annotation n'est pas nullable
                    val key = extract<String>("key", extraAnnotation)
                    val defaultString = extract<String>("defaultString", extraAnnotation)
                    val defaultBoolean = extract<Boolean>("defaultBoolean", extraAnnotation)
                    val defaultStringArray = extract<List<String>>("defaultStringArray", extraAnnotation)
                    val defaultInt = extract<Int>("defaultInt", extraAnnotation)
                    val required = extract<Boolean>("required", extraAnnotation)
                    val type = extract<String>("type", extraAnnotation).toExtraType()
                    val initMode = extract<String>("initMode", extraAnnotation).toInitMode()

                    val extra = when (type) {
                        STRING -> StringExtra(key, required, initMode, defaultString)
                        BOOLEAN -> BooleanExtra(key, required, initMode, defaultBoolean)
                        STRING_ARRAY -> StringArrayExtra(key, required, initMode, defaultStringArray)
                        INT -> IntExtra(key, required, initMode, defaultInt)
                    }

                    // add extra to the list
                    listExtras.add(extra)
                }
            }

        return listExtras
    }

    private fun <T> extract(key: String, extraAnnotation: KSAnnotation): T {
        logger.info(extraAnnotation.arguments.joinToString(","))
        return extraAnnotation.arguments.find { it.name?.asString() == key }?.value as T
    }

    private fun <String> String.toExtraType(): ExtraType {
        return ExtraType.valueOf((this as KSClassDeclaration).simpleName.asString())
    }

    private fun <String> String.toInitMode(): ExtraInitMode {
        return ExtraInitMode.valueOf((this as KSClassDeclaration).simpleName.asString())
    }
}

class ExtrasProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ExtrasProcessor(
            environment.codeGenerator,
            environment.logger,
        )
    }
}