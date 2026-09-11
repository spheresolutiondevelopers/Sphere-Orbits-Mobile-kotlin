plugins {
    id("sphere.android.feature")
}

android {
    namespace = "com.orbits.feature.calendar"
}

dependencies {
    // Domain modules
    implementation(project(":core:domain:calendar"))
    implementation(project(":core:domain:tasks"))
    implementation(project(":core:domain:auth"))

    // Core modules
    implementation(project(":core:theme"))
}
