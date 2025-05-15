plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    kotlin("plugin.serialization") version "1.8.0"

    id("com.apollographql.apollo3") version "4.0.0-beta.7"
}

android {
    namespace = "com.log.network"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

val ktor_version: String by project


dependencies {
    implementation(project(":data"))

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.android)  // или libs.lifecycle.viewmodel.android
    implementation(libs.androidx.lifecycle.viewmodel.ktx)      // или libs.lifecycle.viewmodel.ktx
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Kotlin Extensions
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)

    // Ktor - HTTP Client
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.websockets)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)

    //apollo graphql
    api("com.apollographql.apollo3:apollo-runtime:4.0.0-beta.7")
    api("com.apollographql.apollo3:apollo-engine-ktor:4.0.0-beta.7")
    api("com.apollographql.apollo3:apollo-rx3-support:4.0.0-beta.7")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

apollo {
    service("service") {
        packageName.set("com.example.graphql")
        schemaFile.set(file("src/main/graphql/schema.graphqls"))
        generateOptionalOperationVariables.set(false)
    }
}

// Добавляем задачу для генерации кода Apollo перед компиляцией
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    dependsOn("generateApolloSources")
}