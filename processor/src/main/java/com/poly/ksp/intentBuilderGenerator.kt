import com.poly.annotation.ExtraData
import com.poly.annotation.ExtraType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

fun generateIntentBuilder(packageName: String, className: String, extrasData: List<ExtraData>): String {
    val intentClass = ClassName("android.content", "Intent")
    val arrayListClass = ClassName("java.util", "ArrayList")

    // Créer la propriété intent avec le bon type (Intent)
    val intentProperty = PropertySpec.builder("intent", intentClass)
        .addModifiers(KModifier.PRIVATE)
        .initializer("Intent(action)")
        .build()

    // Fonctions withXxx
    val withMethods = extrasData.map { extra ->
        val key = extra.key
        val paramType = getPropertyType(extra)
        val methodName = "with${key.replaceFirstChar { it.uppercase() }}"

        FunSpec.builder(methodName)
            .addParameter("value", paramType)
            .returns(ClassName(packageName, className)).apply {
                when (extra) {
                    is ExtraData.StringArrayExtra -> addStatement("intent.putStringArrayListExtra(%S, %T(value.toList()))", key, arrayListClass)
                    else -> addStatement("intent.putExtra(%S, value)", key)
                }
                addStatement("return this")
            }
            .build()
    }

    // Créer la méthode build()
    val buildMethod = FunSpec.builder("build")
        .returns(intentClass)
//        .apply {
//            // Vérifier les extras requis
//            extrasData.filter { it.required }.forEach { extra ->
//                val key = extra.key
//                addStatement("if (!intent.hasExtra(%S)) {", key)
//                addStatement("    throw %T(%S)", IllegalStateException::class, "Required extra $key is missing")
//                addStatement("}")
//            }
//        }
        .addStatement("return intent")
        .build()

    // Créer la classe TargetActivityIntentBuilder
    val builderClass = TypeSpec.classBuilder(className)
        .primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameter("action", String::class.asTypeName())
                .build()
        )
        .addProperty(intentProperty)
        .addFunctions(withMethods)
        .addFunction(buildMethod)
        .build()

    // Créer le fichier avec les imports
    val fileSpec = FileSpec.builder(packageName, "${className}IntentBuilder") // Corriger le nom du fichier
        .addType(builderClass)
        .indent("\t")
        .addImport("android.content", "Intent")
        .addImport("java.util", "ArrayList")
        .build()

    return fileSpec.toString()
}

fun getPropertyType(type: ExtraData): TypeName {
    return when (type) {
        is ExtraData.BooleanExtra -> Boolean::class.asTypeName()
        is ExtraData.StringExtra -> String::class.asTypeName()
        is ExtraData.StringArrayExtra -> List::class.asClassName().parameterizedBy(String::class.asTypeName())
        is ExtraData.IntExtra -> Int::class.asTypeName()
    }
}