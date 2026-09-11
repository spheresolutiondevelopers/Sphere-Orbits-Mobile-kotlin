plugins {
    id("sphere.android.library")
}

android {
    namespace = "com.orbits.domain.settings"
}

dependencies {
    implementation(libs.kt.stdlib)
    implementation(libs.kt.coroutines)
    implementation(project(":core:common"))
    testImplementation(libs.bundles.test)
}
