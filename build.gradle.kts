plugins {

    // Android plugins (stable for Material 3)
    id("com.android.application") version "8.11.0" apply false
    id("com.android.library") version "8.11.0" apply false

    // Kotlin plugin (required internally by AGP even if Java)
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}

// -------------------------------
// CLEAN TASK
// -------------------------------
tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}