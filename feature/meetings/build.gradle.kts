plugins {
    id("sphere.android.feature")
}

android {
    namespace = "com.orbits.feature.meetings"
}

dependencies {
    // Domain modules
    implementation(project(":core:domain:meetings"))
    implementation(project(":core:domain:tasks"))
    implementation(project(":core:domain:auth"))

    // Core modules
    implementation(project(":core:theme"))
}
