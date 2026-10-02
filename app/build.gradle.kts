plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
}

android {
    namespace = "com.example.aguardapp"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.aguardapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

// El mapa de sector (MapLibre) viene con el renderizador Vulkan por defecto, y en emuladores y celulares sin
// Vulkan la app se cierra al abrir "Sector" (vk::createInstanceUnique: ErrorInitializationFailed).
// Se usa la variante OpenGL ES de la misma versión, que trae las mismas clases y funciona en todos.
val maplibreOpengl = libs.versions.maplibreOpengl.get()
configurations.configureEach {
    resolutionStrategy.dependencySubstitution {
        substitute(module("org.maplibre.gl:android-sdk"))
            .using(module("org.maplibre.gl:android-sdk-opengl:$maplibreOpengl"))
            .because("Vulkan no está disponible en todos los dispositivos")
    }
}

dependencies {
    // Android y Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Datos locales y tareas en segundo plano
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.work.runtime)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)

    // Inyección de dependencias
    implementation(libs.koin.core)

    // Nube (Supabase) e inicio de sesión con Google
    implementation(libs.supabase.auth)
    implementation(libs.supabase.postgrest)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.multiplatform.settings)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services)
    implementation(libs.googleid)

    // Mapa, cámara, permisos y OCR
    implementation(libs.maplibre.compose)
    implementation(libs.filekit.dialogs.compose)
    implementation(libs.moko.permissions)
    implementation(libs.moko.permissions.compose)
    implementation(libs.moko.permissions.camera)
    implementation(libs.mlkit.text.recognition)

    testImplementation(libs.junit)
}
