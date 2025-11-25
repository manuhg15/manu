// Archivo: app/build.gradle.kts (module level)

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // ✅ CRÍTICO: Aplicar el plugin de Google Services
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.sistemadetaxis" // Asegúrate que este sea tu package name
    compileSdk = 36 // Asegúrate de usar la versión correcta

    defaultConfig {
        applicationId = "com.example.sistemadetaxis"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    // Configuración para Compose
    buildFeatures {
        compose = true
    }
    // Con Kotlin 2.0+ y el plugin de compose, ya no es necesario configurar kotlinCompilerExtensionVersion manualmente aquí
    // si se está usando el plugin org.jetbrains.kotlin.plugin.compose
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10" 
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // --- DEPENDENCIAS DE COMPOSE FUNDAMENTALES ---

    // ✅ IMPORTANTE: Agregar el BOM de Compose para gestionar versiones automáticamente
    implementation(platform(libs.androidx.compose.bom))

    // Soporte para la actividad principal y Compose
    implementation("androidx.activity:activity-compose:1.8.2")
    
    // Ahora estas dependencias tomarán la versión del BOM
    // Compose Runtime (remember, mutableStateOf)
    implementation("androidx.compose.runtime:runtime")
    // Base de la UI (Layouts, Modifiers)
    implementation("androidx.compose.ui:ui")
    // Componentes de diseño (Scaffold, Text, Button, etc.)
    implementation("androidx.compose.material3:material3")
    // Funcionalidades de bajo nivel (Scroll, Background)
    implementation("androidx.compose.foundation:foundation")
    
    // Soporte para previsualización en el IDE
    debugImplementation("androidx.compose.ui:ui-tooling")
    // Soporte para las previsualizaciones en runtime
    implementation("androidx.compose.ui:ui-tooling-preview")

    // --- DEPENDENCIAS DE FIREBASE Y ASINCRONÍA ---

    // FIREBASE BOM (Bill of Materials) - Mantiene las versiones consistentes
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))

    // FIREBASE AUTHENTICATION (Para registro y login)
    implementation("com.google.firebase:firebase-auth-ktx")

    // FIREBASE FIRESTORE (Base de datos en línea)
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Kotlin Coroutines (Necesario para las operaciones asíncronas de Firebase)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services")

    // Navegación con Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")
}