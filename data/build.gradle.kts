import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp) // Apply the KSP plugin
    alias(libs.plugins.hilt) // Apply the Hilt Android plugin
    alias(libs.plugins.kotlin.serialize) // Add Kotlin Serialization plugin
}

android {
    namespace = "com.example.androidroadmap.data"
    compileSdk = 36

    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val properties = Properties()
        val exists = project.rootProject.file("local.properties").exists()
        if (exists) {
            properties.load(project.rootProject.file("local.properties").inputStream())
            print("local.properties found")
        }
        val apiKey = properties.getProperty("openweathermap.api_key")
            ?: error("Missing openweathermap.api_key in local.properties")

        buildConfigField(
            "String",
            "OPEN_WEATHER_API_KEY",
            "\"$apiKey\""
        )
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":models"))
    implementation(project(":domain"))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.play.services.location)
    implementation(libs.cronet.api)
    ksp(libs.androidx.room.compiler) // Use ksp for Room compiler

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler) // Use ksp for Hilt compiler

    // Kotlin Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Testing
    testImplementation(libs.mockk)
    testImplementation("junit:junit:4.13.2")
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.room.testing)

    implementation(libs.retrofit)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.kotlinx.serialization.converter)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    implementation("com.google.android.gms:play-services-cronet:18.0.1")
    implementation("com.google.net.cronet:cronet-okhttp:0.1.0") {
        exclude(group = "org.chromium.net", module = "cronet-common")
        exclude(group = "org.chromium.net", module = "cronet-api")
        exclude(group = "org.chromium.net", module = "cronet-shared")
    }

}
