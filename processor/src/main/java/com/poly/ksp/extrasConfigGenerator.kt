package com.poly.ksp

import com.poly.annotation.ExtraData
import com.poly.annotation.ExtraData.*
import com.poly.annotation.ExtraData.StringArrayExtra
import com.poly.annotation.ExtraInitMode.*
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import javax.inject.Inject
import javax.inject.Singleton

fun generateExtrasConfigClass(
    packageName: String,
    className: String,
    extrasData: List<ExtraData>
): String {

    // Map des valeurs par défaut
    val defaultKeysMap = PropertySpec.builder(
        "defaultKeys",
        Map::class.asTypeName()
            .parameterizedBy(String::class.asTypeName(), Any::class.asTypeName().copy(nullable = true))
    )
        .addModifiers(KModifier.PRIVATE)
        .initializer(
            CodeBlock.builder().apply {
                add("mapOf(\n")
                extrasData.forEach { extra ->
                    val key = extra.key
                    val defaultValue = getDefaultValue(extra)

                    when (extra) {
                        is BooleanExtra, is IntExtra -> add("%S to %L,\n", key, defaultValue)
                        is StringExtra -> add("%S to %S,\n", key, defaultValue)
                        is StringArrayExtra -> {
                            if (defaultValue != null) {
                                val values = (defaultValue as List<String>)
                                    .joinToString(", ") { "\"$it\"" }
                                add("%S to listOf<String>($values),\n", key)
                            } else {
                                add("%S to null,\n", key)
                            }
                        }
                    }
                }
                add(")\n")
            }.build()
        )
        .build()

    // Map des required keys
    val requiredKeysMap = PropertySpec.builder(
        "requiredKeys",
        Map::class.parameterizedBy(String::class, Boolean::class)
    )
        .addModifiers(KModifier.PRIVATE)
        .initializer(
            CodeBlock.builder().apply {
                add("mapOf(\n")
                extrasData.forEach { extra ->
                    add("%S to %L,\n", extra.key, extra.required)
                }
                add(")\n")
            }.build()
        )
        .build()

    // Propriétés par clé
    val properties = extrasData.map { extra ->
        val type = getPropertyType(extra)
        val initializer = when (extra) {
            is BooleanExtra, is IntExtra -> CodeBlock.of("%L", getDefaultValue(extra))
            is StringExtra -> CodeBlock.of("%S", getDefaultValue(extra))
            is StringArrayExtra -> {
                getDefaultValue(extra)?.let { list ->
                    val joined = (list as List<String>).joinToString(", ") { "\"$it\"" }
                    CodeBlock.of("listOf<String>($joined)")
                } ?: CodeBlock.of("null")
            }
        }

        PropertySpec.builder(extra.key, type)
            .mutable(true)
            .initializer(initializer)
            .build()
    }

    // toMap()
    val toMapFun = FunSpec.builder("toMap")
        .returns(Map::class.asTypeName().parameterizedBy(String::class.asTypeName(), Any::class.asTypeName().copy(nullable = true)))
        .addCode(
            CodeBlock.builder().apply {
                add("return mapOf(\n")
                extrasData.forEach { extra ->
                    add("\t%S to %L,\n", extra.key, extra.key)
                }
                add(")\n")
            }.build()
        )
        .build()

    // toString()
    val toPrettyLogFun = FunSpec.builder("toString")
        .addModifiers(KModifier.OVERRIDE)
        .returns(String::class)
        .addCode("return toMap().entries.joinToString(\"\\n\") { it.key + \" = \" + it.value.toString() }\n")
        .build()


    // initialize(Intent)
    val initializeFun = FunSpec.builder("initialize")
        .addParameter("intent", ClassName("android.content", "Intent"))
        .apply {
            extrasData.forEach { extra ->
                val cast = when (extra) {
                    is BooleanExtra -> "as " + getPropertyType(extra).toString()
                    is StringExtra -> "as " + getPropertyType(extra).toString()
                    is StringArrayExtra -> "as " + getPropertyType(extra).toString()
                    is IntExtra -> "as " + getPropertyType(extra).toString()
                }

                val key = extra.key
                val type = getPropertyType(extra)
                val checkType = getCheckPropertyType(extra)

                addCode("// %S\n", key)
                beginControlFlow("if (intent.hasExtra(%S))", key)
                beginControlFlow("try")
                beginControlFlow("if (intent.extras?.get(%S) !is %T)", key, checkType)
                addStatement("throw TypeCastExtraException(%S)", "L'extra $key attendu : $checkType")
                endControlFlow()

                val assignment = when (extra) {
                    is BooleanExtra -> "intent.getBooleanExtra(%S, false)"
                    is IntExtra -> "intent.getIntExtra(%S, 0)"
                    is StringExtra -> "intent.getStringExtra(%S)"
                    is StringArrayExtra -> "intent.getStringArrayListExtra(%S)?.toList()"
                }
                addStatement("%L = $assignment %L", key, key, cast)
                nextControlFlow("catch (e: Exception)")
                addStatement("throw TypeCastExtraException(%S)", "L'extra $key attendu : $checkType")
                endControlFlow()
                endControlFlow()

                // Vérif required
                addCode("// Vérification si %S est obligatoire\n", key)
                beginControlFlow("if (requiredKeys[%S] == true && !intent.hasExtra(%S))", key, key)
                addStatement("throw MissingRequiredExtraException(%S)", "L'extra $key est manquant et obligatoire")
                endControlFlow()
            }
        }
        .build()

    val suppressAnnotation = AnnotationSpec.builder(Suppress::class)
        .addMember("%S", "DEPRECATION")
        .build()

    val extrasConfigClass = TypeSpec.classBuilder(className)
        .addAnnotation(suppressAnnotation)
        .addAnnotation(Singleton::class)
        .primaryConstructor(FunSpec.constructorBuilder().addAnnotation(Inject::class).build())
        .addProperty(requiredKeysMap)
        .addProperty(defaultKeysMap)
        .addProperties(properties)
        .addFunction(initializeFun)
        .addFunction(toMapFun)
        .addFunction(toPrettyLogFun)
        .build()

    return FileSpec.builder(packageName, className)
        .addType(extrasConfigClass)
        .indent("\t") // 4 espaces
        .addImport("android.content", "Intent")
        .addImport("com.poly.annotation", "MissingRequiredExtraException", "TypeCastExtraException")
        .addImport("javax.inject", "Inject", "Singleton")
        .build()
        .toString()
}

fun getPropertyType(extra: ExtraData): TypeName {
    return when (extra) {
        is BooleanExtra -> Boolean::class.asTypeName().copy(nullable = extra.init == NULL)
        is StringExtra -> String::class.asTypeName().copy(nullable = extra.init == NULL)
        is StringArrayExtra -> List::class.asClassName().parameterizedBy(String::class.asTypeName()).copy(nullable = extra.init == NULL)
        is IntExtra -> Int::class.asTypeName().copy(nullable = extra.init == NULL)
    }
}

fun getCheckPropertyType(extra: ExtraData): TypeName {
    return when (extra) {
        is BooleanExtra -> Boolean::class.asTypeName()
        is StringExtra -> String::class.asTypeName()
        is StringArrayExtra -> List::class.asTypeName().parameterizedBy(STAR)
        is IntExtra -> Int::class.asTypeName()
    }
}

fun getDefaultValue(extra: ExtraData): Any? {
    return when (extra) {
        is BooleanExtra -> if (extra.init == NULL) null else extra.default
        is StringExtra -> if (extra.init == NULL) null else extra.default
        is StringArrayExtra -> if (extra.init == NULL) null else extra.default
        is IntExtra -> if (extra.init == NULL) null else extra.default
    }
}