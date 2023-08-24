plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "io.hlab.vectormap.compose"
    compileSdk = 34

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    kotlin {
        jvmToolchain(17)
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
    }
}

dependencies {
    api(libs.kakao.vectormap)

    implementation(libs.kotlin.coroutines.android)
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.androidx.compose.foundation)
}