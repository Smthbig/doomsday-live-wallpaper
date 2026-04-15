import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application") version "8.11.0"
}

val keystorePropsFile = rootProject.file("release.properties")
val keystoreProps = Properties()

if (keystorePropsFile.exists()) {
    FileInputStream(keystorePropsFile).use {
        keystoreProps.load(it)
    }
}

// -------------------------------
// DETECT ENV (CI)
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

android {
    namespace = "com.devkrishna.doomsday"
    compileSdk = 34

    lint {
        checkReleaseBuilds = false
    }

    // -------------------------------
    // SIGNING CONFIG
    // -------------------------------
    signingConfigs {

        // CI signing (PRIORITY)
        if (hasEnvSigning) {
            create("release") {
                storeFile = rootProject.file(envKeystoreFile!!)
                storePassword = envStorePassword
                keyAlias = envKeyAlias
                keyPassword = envKeyPassword
            }
        }
        // Local signing fallback
        else if (hasLocalSigning) {
            create("release") {
                storeFile = rootProject.file(keystoreProps["storeFile"] as String)
                storePassword = keystoreProps["storePassword"] as String
                keyAlias = keystoreProps["keyAlias"] as String
                keyPassword = keystoreProps["keyPassword"] as String
            }
        }
    }

    defaultConfig {
        applicationId = "com.devkrishna.doomsday"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

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
    }

    buildFeatures {
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
            excludes.add("META-INF/kotlinx_coroutines_core.version")

            pickFirsts.add("nonJvmMain/default/linkdata/package_androidx/0_androidx.knm")
            pickFirsts.add("nonJvmMain/default/linkdata/root_package/0_.knm")
            pickFirsts.add("nonJvmMain/default/linkdata/module")

            pickFirsts.add("nativeMain/default/linkdata/root_package/0_.knm")
            pickFirsts.add("nativeMain/default/linkdata/module")

            pickFirsts.add("commonMain/default/linkdata/root_package/0_.knm")
            pickFirsts.add("commonMain/default/linkdata/module")
            pickFirsts.add("commonMain/default/linkdata/package_androidx/0_androidx.knm")

            pickFirsts.add("META-INF/kotlin-project-structure-metadata.json")

            merges.add("commonMain/default/manifest")
            merges.add("nonJvmMain/default/manifest")
            merges.add("nativeMain/default/manifest")
        }
    }

    configurations.all {
        resolutionStrategy {
            force("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.22")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.22")

            force("androidx.collection:collection:1.4.2")
            force("androidx.annotation:annotation:1.8.1")
            force("androidx.core:core-ktx:1.8.0")
            force("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
            force("androidx.collection:collection-ktx:1.4.2")
        }
    }
}

// -------------------------------
// VERSION TASKS (FOR CI)
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

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
}

dependencies {
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.startup:startup-runtime:1.1.1")
    implementation("androidx.interpolator:interpolator:1.0.0")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.zxing:core:3.5.3")
}