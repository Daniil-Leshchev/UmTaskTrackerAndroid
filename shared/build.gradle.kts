import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("com.android.kotlin.multiplatform.library")
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidLibrary {
        namespace = "com.umschool.umtasktracker.shared"
        compileSdk = 35
        minSdk = 26
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    // Настройка JVM target для Android
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)

            // Kotlinx
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)

            // Koin
            implementation(libs.koin.core)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.androidx.security.crypto)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}
