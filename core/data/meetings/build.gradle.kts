plugins {
    id("sphere.android.library")
}

android {
    namespace = "com.orbits.data.meetings"
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.serialization)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Internal modules
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:domain:meetings"))

    // Zoom API (simplified - would use actual SDK in production)
    // implementation("us.zoom.sdk:zoom-sdk:5.16.6")
    
    // Microsoft Teams integration
    // implementation("com.microsoft.graph:microsoft-graph:5.68.0")

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
}