plugins {
    id("sphere.android.feature")
}

android {
    namespace = "com.orbits.feature.events"
}

dependencies {
    implementation(project(":core:theme"))
    implementation(project(":core:domain:events"))
}
