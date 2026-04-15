import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application") version "8.11.0"
}

// -------------------------------
// LOAD KEYSTORE PROPERTIES
// -------------------------------
val keystorePropsFile = rootProject.file("release.properties")
val keystoreProps = Properties()

if (keystorePropsFile.exists()) {
    FileInputStream(keystorePropsFile).use {
        keystoreProps.load(it)
    }
}

// -------------------------------
// ENV SIGNING (CI)
// -------------------------------
val envKeystoreFile = System.getenv("KEYSTORE_FILE")
val envStorePassword = System.getenv("KEYSTORE_PASSWORD")
val envKeyAlias = System.getenv("KEY_ALIAS")
val envKeyPassword = System.getenv("KEY_PASSWORD")

val hasEnvSigning = !envKeystoreFile.isNullOrEmpty() &&
        !envStorePassword.isNullOrEmpty() &&
        !envKeyAlias.isNullOrEmpty() &&
        !envKeyPassword.isNullOrEmpty()

val hasLocalSigning = keystorePropsFile.exists() &&
        listOf("storeFile", "storePassword", "keyAlias", "keyPassword").all {
            keystoreProps[it] != null
        }

// -------------------------------
// ANDROID CONFIG
// -------------------------------
android {

    namespace = "com.devkrishna.doomsday"

    compileSdk = 36

    defaultConfig {
        applicationId = "com.devkrishna.doomsday"
        minSdk = 28
        targetSdk = 36

        versionCode = 1
        versionName = "1.3"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    lint {
        checkReleaseBuilds = false
    }

    // -------------------------------
    // SIGNING CONFIG
    // -------------------------------
    signingConfigs {

        if (hasEnvSigning) {
            create("release") {
                storeFile = rootProject.file(envKeystoreFile!!)
                storePassword = envStorePassword
                keyAlias = envKeyAlias
                keyPassword = envKeyPassword
            }
        } else if (hasLocalSigning) {
            create("release") {
                storeFile = rootProject.file(keystoreProps["storeFile"] as String)
                storePassword = keystoreProps["storePassword"] as String
                keyAlias = keystoreProps["keyAlias"] as String
                keyPassword = keystoreProps["keyPassword"] as String
            }
        }
    }

    // -------------------------------
    // BUILD TYPES
    // -------------------------------
    buildTypes {

        release {

            if (signingConfigs.names.contains("release")) {
                signingConfig = signingConfigs.getByName("release")
            }

            isMinifyEnabled = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            isMinifyEnabled = false
        }
    }

    // -------------------------------
    // JAVA CONFIG
    // -------------------------------
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // -------------------------------
    // FEATURES
    // -------------------------------
    buildFeatures {
        viewBinding = true
    }

    // -------------------------------
    // PACKAGING FIXES
    // -------------------------------
    packaging {
        resources {

            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/kotlinx_coroutines_core.version"

            pickFirsts += "META-INF/kotlin-project-structure-metadata.json"

            // Kotlin metadata fixes
            pickFirsts += "commonMain/default/linkdata/root_package/0_.knm"
            pickFirsts += "commonMain/default/linkdata/module"

            pickFirsts += "nonJvmMain/default/linkdata/root_package/0_.knm"
            pickFirsts += "nonJvmMain/default/linkdata/module"

            pickFirsts += "nativeMain/default/linkdata/root_package/0_.knm"
            pickFirsts += "nativeMain/default/linkdata/module"

            pickFirsts += "nonJvmMain/default/linkdata/package_androidx/0_androidx.knm"
            pickFirsts += "commonMain/default/linkdata/package_androidx/0_androidx.knm"
        }
    }
}

// -------------------------------
// VERSION TASKS (CI)
// -------------------------------
tasks.register("printVersionName") {
    doLast {
        println(android.defaultConfig.versionName)
    }
}

tasks.register("printVersionCode") {
    doLast {
        println(android.defaultConfig.versionCode)
    }
}

// -------------------------------
// JAVA WARNINGS
// -------------------------------
tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
}

// -------------------------------
// DEPENDENCIES
// -------------------------------
dependencies {

    // Core AndroidX
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")

    // Material 3
    implementation("com.google.android.material:material:1.11.0")

    // Layout
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Lifecycle (stability)
    implementation("androidx.lifecycle:lifecycle-runtime:2.7.0")

    // Startup
    implementation("androidx.startup:startup-runtime:1.1.1")

    // Animation
    implementation("androidx.interpolator:interpolator:1.0.0")

    // QR
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.zxing:core:3.5.3")
}