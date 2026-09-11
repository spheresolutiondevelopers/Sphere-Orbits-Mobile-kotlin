plugins {
    id("sphere.android.feature")
}

android {
    namespace = "com.orbits.feature.chat"
}

dependencies {
    // Domain modules
    implementation(project(":core:domain:chat"))
    implementation(project(":core:domain:auth"))

    // Core modules
    implementation(project(":core:theme"))

    // Coil for images
    implementation("io.coil-kt:coil-compose:2.5.0")
}
