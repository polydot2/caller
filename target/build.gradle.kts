plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.open.api)
}

ksp {
    arg("ksp.incremental.log", "true") // Log KSP
}

android {
    namespace = "com.poly.target"
    compileSdk = 36
    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.17"
    }
    kotlin {
        jvmToolchain(21)
        sourceSets.main {
            kotlin.srcDir(file("build/generated/ksp/kotlin"))
            kotlin.srcDir(file("build/generated/source/openapi"))
        }
    }
}

openApiGenerate {
    generatorName.set("kotlin")
    inputSpec.set("$projectDir/spec/openapi.yaml")
    outputDir.set("$buildDir/generated/source/openapi")
    modelPackage.set("com.poly.target.models")
    globalProperties.set(
        mapOf(
            "apis" to "false",
            "models" to "",
            "modelTests" to "false"
        )
    )
    configOptions.set(
        mapOf(
            "serializableModel" to "true",
            "generateBuilders" to "true"
        )
    )
}

dependencies {
    implementation(project(":annotation"))
    ksp(project(":processor"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.square.moshi)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

}

tasks.named("build") {
    dependsOn(tasks.named("openApiGenerate"))
}

//tasks.named("kspKotlin") {
//    dependsOn(tasks.named("openApiGenerate"))
//}