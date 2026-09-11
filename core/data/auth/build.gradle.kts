plugins {
    id("sphere.android.library")
}

android {
    namespace = "com.orbits.data.auth"
}

dependencies {
    // Internal modules
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:domain:auth"))

    // Google Auth
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // DataStore
    implementation(libs.datastore.preferences)
    implementation(libs.datastore.core)
}