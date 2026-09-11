plugins {
    id("sphere.android.feature")
}

android {
    namespace = "com.orbits.feature.analytics"
}

dependencies {
    // Domain modules
    implementation(project(":core:domain:analytics"))
    implementation(project(":core:domain:tasks"))
    implementation(project(":core:domain:auth"))

    // Core modules
    implementation(project(":core:theme"))

    // Charts
    implementation("co.yml:ycharts:2.1.0")
}
