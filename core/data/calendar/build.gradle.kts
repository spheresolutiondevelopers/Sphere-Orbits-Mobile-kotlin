plugins {
    id("sphere.android.library")
}

android {
    namespace = "com.orbits.data.calendar"
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
    implementation(project(":core:domain:calendar"))

    // Google Calendar API
    implementation("com.google.apis:google-api-services-calendar:v3-rev20250128-2.0.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.23.0")

    // Microsoft Graph API (Outlook)
    implementation("com.microsoft.graph:microsoft-graph:5.68.0")
    implementation("com.azure:azure-identity:1.12.2")

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
}