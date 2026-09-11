plugins {
    id("sphere.android.library")
}

android {
    namespace = "com.orbits.core.sync"
}

dependencies {
    // WorkManager & Hilt Work
    implementation(libs.workmanager)
    implementation(libs.hilt.work)
    ksp(libs.hilt.work.compiler)

    // Internal modules
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))

    // All domain modules (for entity references)
    implementation(project(":core:domain:auth"))
    implementation(project(":core:domain:tasks"))
    implementation(project(":core:domain:calendar"))
    implementation(project(":core:domain:events"))
    implementation(project(":core:domain:meetings"))
    implementation(project(":core:domain:appointments"))
    implementation(project(":core:domain:chat"))
    implementation(project(":core:domain:notes"))
    implementation(project(":core:domain:analytics"))
    implementation(project(":core:domain:settings"))

    // All data modules (for repository implementations)
    implementation(project(":core:data:auth"))
    implementation(project(":core:data:tasks"))
    implementation(project(":core:data:calendar"))
    implementation(project(":core:data:events"))
    implementation(project(":core:data:meetings"))
    implementation(project(":core:data:appointments"))
    implementation(project(":core:data:chat"))
    implementation(project(":core:data:notes"))
    implementation(project(":core:data:analytics"))
    implementation(project(":core:data:settings"))

    // Testing
    testImplementation(libs.workmanager.testing)
}
