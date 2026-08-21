plugins {
    id("sphere.android.library")
}

android {
    namespace = "com.orbits.domain.tasks"
}

dependencies {
    // Kotlin only — NO Android dependencies
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.coroutines)

    // Internal modules
    implementation(project(":core:common"))

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
}