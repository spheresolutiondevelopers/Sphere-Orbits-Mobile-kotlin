plugins {
    id("sphere.android.feature")
}

android {
    namespace = "com.orbits.feature.dashboard"
}

dependencies {
    // Domain modules
    implementation(project(":core:domain:tasks"))
    implementation(project(":core:domain:calendar"))
    implementation(project(":core:domain:appointments"))
    implementation(project(":core:domain:meetings"))
    implementation(project(":core:domain:analytics"))
    implementation(project(":core:domain:auth"))

    // Core modules
    implementation(project(":core:theme"))
}
