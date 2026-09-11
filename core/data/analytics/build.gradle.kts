plugins {
    id("sphere.android.library")
}

android {
    namespace = "com.orbits.data.analytics"
}

dependencies {
    // Internal modules
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:domain:analytics"))

    // For report generation (PDF/CSV)
    implementation(libs.html.parser)
    implementation(libs.opencsv)
    implementation(libs.itext.core)
}