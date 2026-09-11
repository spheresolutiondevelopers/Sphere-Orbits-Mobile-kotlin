plugins {
    id("sphere.android.library")
}

android {
    namespace = "com.orbits.domain.events"
}

dependencies {
    implementation(libs.kt.stdlib)
    implementation(libs.kt.coroutines)
    implementation(project(":core:common"))
    testImplementation(libs.bundles.test)
}
