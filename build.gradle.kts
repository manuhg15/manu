// Archivo: build.gradle.kts (project level)

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    //  CRÍTICO: Definición del plugin de Google Services
    // Esto conecta tu proyecto con Firebase antes de compilar.
    id("com.google.gms.google-services") version "4.4.1" apply false
}