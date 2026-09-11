plugins {
    id("sphere.android.feature")
}

android {
    namespace = "com.orbits.feature.tasks"
}

dependencies {
    // Domain modules
    implementation(project(":core:domain:tasks"))
    implementation(project(":core:domain:auth"))

    // Core modules
    implementation(project(":core:theme"))
}
