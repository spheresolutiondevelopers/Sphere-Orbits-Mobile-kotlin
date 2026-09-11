plugins {
    id("sphere.android.library")
}

android {
    namespace = "com.orbits.data.appointments"
}

dependencies {
    // Internal modules
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:domain:appointments"))
}