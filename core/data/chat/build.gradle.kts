plugins {
    id("sphere.android.library")
}

android {
    namespace = "com.orbits.data.chat"
}

dependencies {
    // Internal modules
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:domain:chat"))

    // WebSocket support
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}