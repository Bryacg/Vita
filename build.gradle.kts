plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.android.library) apply false
}
// Recomendado: Configuración global para todos los módulos (opcional pero útil)


// Recomendado: Evita warnings de resolución de dependencias y fuerza versiones consistentes si es necesario
subprojects {
    afterEvaluate {
        configurations.all {
            resolutionStrategy {
                // Ejemplo: fuerza versiones específicas si hay conflictos (comenta si no necesitas)
                // force("androidx.core:core-ktx:1.13.1")
                // force("org.jetbrains.kotlin:kotlin-stdlib:2.0.0") // Si usas Kotlin 2.0
            }
        }
    }
}

