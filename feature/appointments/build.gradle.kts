plugins {
    id("sphere.android.feature")
}

android {
    namespace = "com.orbits.feature.appointments"
}

dependencies {
    // Domain modules
    implementation(project(":core:domain:appointments"))
    implementation(project(":core:domain:auth"))
    implementation(project(":core:domain:calendar"))

    // Core modules
    implementation(project(":core:theme"))
}
