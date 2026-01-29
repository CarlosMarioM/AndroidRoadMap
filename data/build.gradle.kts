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

    defaultConfig {
        minSdk = 24
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
}
