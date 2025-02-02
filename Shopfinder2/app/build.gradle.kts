plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.shopfinder2"
    compileSdk = 34 // Updated to a more stable version

    defaultConfig {
        applicationId = "com.example.shopfinder2"
        minSdk = 24
        targetSdk = 34 // Match this with compileSdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true // Optional: Enables ViewBinding
    }
}

dependencies {
    // Core dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation("androidx.constraintlayout:constraintlayout:2.1.4") // Latest stable version

    // Firebase dependencies
    implementation("com.google.firebase:firebase-auth:21.0.1")  // Firebase Authentication
    implementation("com.google.firebase:firebase-database:20.0.3")  // Firebase Realtime Database

    // Play Services dependencies
    implementation("com.google.android.gms:play-services-maps:18.1.0") // Maps
    implementation("com.google.android.gms:play-services-location:21.0.1") // Location Services
    implementation("com.google.android.libraries.places:places:2.7.0") // Places API

    // Splash Screen Support
    implementation("androidx.core:core-splashscreen:1.0.1")

    // AppCompat Library for backward compatibility
    implementation("androidx.appcompat:appcompat:1.4.0")

    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
