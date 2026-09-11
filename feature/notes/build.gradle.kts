plugins {
    id("sphere.android.feature")
}

android {
    namespace = "com.orbits.feature.notes"
}

dependencies {
    // Domain modules
    implementation(project(":core:domain:notes"))
    implementation(project(":core:domain:auth"))

    // Core modules
    implementation(project(":core:theme"))

    // Markdown rendering
    implementation(libs.markwon.core)
    implementation(libs.markwon.ext.tables)
    implementation(libs.markwon.ext.strikethrough)
}
