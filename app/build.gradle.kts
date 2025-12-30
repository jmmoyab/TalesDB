
plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 21
        targetSdk = 33
        versionCode = 4
        versionName = "1.2.2"

        vectorDrawables {
            useSupportLibrary = true
        }

    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    signingConfigs {
        create("release") {
            storeFile = file("keystore/talesdb-release.jks")
            storePassword = "talesdb2025"
            keyAlias = "talesdb-key"
            keyPassword = "talesdb2025"
        }
    }

    buildTypes {
        debug {
            // Firmamos debug con el keystore release para distribución
            signingConfig = signingConfigs.getByName("release")
        }

        release {
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("release")
        }
    }

    buildFeatures {
        viewBinding = true
        
    }
    
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "11"
}

dependencies {
    // Core Android
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.recyclerview:recyclerview:1.3.0")

    // Fragment y ViewModel
    implementation("androidx.fragment:fragment-ktx:1.6.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")

    // Gson para serialización JSON
    implementation("com.google.code.gson:gson:2.10.1")

    // Room Database - TEMPORALMENTE DESACTIVADO para compilar en AndroidIDE
    // implementation("androidx.room:room-runtime:2.4.3")
    // implementation("androidx.room:room-ktx:2.4.3")
    // ksp("androidx.room:room-compiler:2.4.3")
}
