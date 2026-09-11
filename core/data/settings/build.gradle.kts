plugins {
    id("sphere.android.library")
}

android {
    namespace = "com.orbits.data.settings"
}

dependencies {
    // DataStore for local preference storage
    implementation(libs.datastore.preferences)
    implementation(libs.datastore.core)

    // Internal modules
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:domain:settings"))
}