plugins {
    id("sphere.android.feature")
}

android {
    namespace = "com.orbits.feature.settings"
}

dependencies {
    // Domain modules
    implementation(project(":core:domain:settings"))
    implementation(project(":core:domain:auth"))
    implementation(project(":core:domain:appointments"))
    implementation(project(":core:domain:calendar"))

    // Core modules
    implementation(project(":core:theme"))

    // DataStore
    implementation(libs.datastore.preferences)
    implementation(libs.datastore.core)

    // Coil for images
    implementation("io.coil-kt:coil-compose:2.5.0")
}
