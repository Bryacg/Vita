plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.vita"
    compileSdk = 35
    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
    defaultConfig {
        applicationId = "com.example.vita"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Recomendado: habilita vector drawables si usas icons modernos
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true           // Actívalo en release para mejor performance
            isShrinkResources = true         // Elimina recursos no usados
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        compose = true
    }


}

dependencies {
    // Core & Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Compose (usando BOM para versiones consistentes)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.foundation)
    implementation(libs.compose.foundation.layout)
    implementation(libs.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.androidx.material3)
    debugImplementation(libs.compose.ui.tooling)

    // Navigation + Hilt Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // Firebase (usa la versión más reciente estable en enero 2026 ~34.7.0+)
    implementation(platform(libs.firebase.bom))  // Actualiza en gradle/libs.versions.toml
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    // Google Sign-In moderno con Credential Manager (recomendado 2025–2026)
    // Evita la versión legacy de play-services-auth (deprecada)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)  // com.google.android.libraries.identity.googleid:googleid

    // Room (con KSP para mejor performance que annotationProcessor)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // Networking (si usas Retrofit para OpenAI o Godot backend)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Serialization (útil para JSON de APIs)
    implementation(libs.kotlinx.serialization.json)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    //Iconos
    implementation("androidx.compose.material:material-icons-extended:1.6.0")
    // SDK oficial de Google Generative AI
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
    //Vico
    implementation("com.patrykandpatrick.vico:compose-m3:1.14.0")
    //godot
    implementation("org.godotengine:godot:4.2.0.stable")
}

// Recomendado: agrega al final para evitar warnings de resolución de dependencias
configurations.all {
    resolutionStrategy {
        force("androidx.core:core-ktx:1.13.1")  // Ejemplo si necesitas forzar alguna versión
    }
}
