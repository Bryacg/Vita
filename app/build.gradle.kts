import java.util.Properties
import java.io.FileInputStream

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
        vectorDrawables.useSupportLibrary = true

        // --- INICIO: LÓGICA PARA VARIABLES OCULTAS ---
        val properties = Properties()
        val propertiesFile = rootProject.file("local.properties")
        if (propertiesFile.exists()) {
            properties.load(FileInputStream(propertiesFile))
        }

        // Leemos cada una desde local.properties
        val mName = properties.getProperty("modelName") ?: ""
        val aChat = properties.getProperty("apiChatb") ?: ""
        val aRetos = properties.getProperty("apiRetos") ?: ""

        // Las registramos en BuildConfig
        buildConfigField("String", "MODEL_NAME", "\"$mName\"")
        buildConfigField("String", "API_CHAT", "\"$aChat\"")
        buildConfigField("String", "API_RETOS", "\"$aRetos\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
        buildConfig = true
    }
}

dependencies {
    // Core & Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Compose
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

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    // Google Sign-In / Credentials
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))

    // Iconos y otros
    implementation("androidx.compose.material:material-icons-extended:1.6.0")
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    // Godot local lib
    implementation(files("libs/godot_lib.aar"))
    implementation("androidx.fragment:fragment:1.3.6")
    implementation("androidx.core:core-splashscreen:1.0.0")

    // Vico Charts
    implementation(libs.vico.core)
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m3)
}

configurations.all {
    resolutionStrategy {
        force("androidx.core:core-ktx:1.13.1")
    }
}
