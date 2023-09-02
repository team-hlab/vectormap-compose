plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.hilt) apply false

    alias(libs.plugins.dokka)
    alias(libs.plugins.kotlin.binaryCompatibility)
}

apiValidation {
    ignoredProjects.addAll(listOf("sample-app"))
    nonPublicMarkers.add("kotlin.PublishedApi")
}