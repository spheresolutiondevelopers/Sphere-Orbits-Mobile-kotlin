plugins {
    id("sphere.android.library")
}

android {
    namespace = "com.orbits.data.meetings"
}

dependencies {
    // Internal modules
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:domain:meetings"))
    implementation(project(":core:domain:tasks"))

    // Networking
    implementation(libs.bundles.network)
    implementation(libs.kt.coroutines)

    // Zoom API (simplified - would use actual SDK in production)
    // implementation("us.zoom.sdk:zoom-sdk:5.16.6")
    
    // Microsoft Teams integration
    // implementation("com.microsoft.graph:microsoft-graph:5.68.0")
}