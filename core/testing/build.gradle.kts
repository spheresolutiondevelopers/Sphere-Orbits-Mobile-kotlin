plugins {
    id("sphere.android.library")
}

android {
    namespace = "com.orbits.core.testing"
}

dependencies {
    implementation(libs.kt.stdlib)
    implementation(libs.bundles.test)
    implementation(libs.bundles.androidTest)
}
