plugins {
    id("sphere.android.library")
}

android {
    namespace = "com.orbits.data.calendar"
}

dependencies {
    // Internal modules
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:domain:calendar"))

    // Google Calendar API
    implementation(libs.google.api.calendar)
    implementation(libs.google.auth.oauth2)

    // Microsoft Graph API (Outlook)
    implementation(libs.microsoft.graph)
    implementation(libs.azure.identity)
}