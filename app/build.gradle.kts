plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialize)
}

android {
    namespace = "com.example.androidroadmap"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.androidroadmap"
        minSdk = 25
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.material3)
    implementation(libs.accompanist.themeadapter)

    implementation(project(":topics"))
    implementation(project(":ui"))
    implementation(libs.androidx.monitor)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.navigation.compose)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    implementation("io.noties.markwon:core:4.6.2")
    implementation("androidx.compose.material:material-icons-core:1.7.8")
// or for extended icons:
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    debugImplementation(libs.androidx.compose.ui.tooling)
}