plugins {
    id("sphere.android.library")
}

android {
    namespace = "com.orbits.domain.analytics"
}

dependencies {
    // Kotlin only — NO Android dependencies
    implementation(libs.kt.stdlib)
    implementation(libs.kt.coroutines)

    // Internal modules
    implementation(project(":core:common"))

    // Testing
    testImplementation(libs.bundles.test)
}
