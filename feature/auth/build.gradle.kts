plugins {
    id("sphere.android.feature")
}

android {
    namespace = "com.orbits.feature.auth"
}

dependencies {
    // Domain modules
    implementation(project(":core:domain:auth"))

    // Core modules
    implementation(project(":core:theme"))
}
