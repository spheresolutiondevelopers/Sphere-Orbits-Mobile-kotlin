plugins {
    id("sphere.android.library")
}

android {
    namespace = "com.orbits.data.analytics"
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.serialization)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Internal modules
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:domain:analytics"))

    // For report generation (PDF/CSV)
    implementation("com.github.psiegman:lightweight-html-parser:1.0.2")
    implementation("com.opencsv:opencsv:5.9")
    implementation("com.itextpdf:itext7-core:7.2.5")

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
}