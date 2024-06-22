plugins {
    id("java-library")
    alias(libs.plugins.jetbrainsKotlinJvm)
    kotlin("plugin.serialization") version "1.8.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlin.stdlib)

    // Lifecycle components

//    implementation(libs.androidx.lifecycle.viewmodel.android)
//    implementation(libs.androidx.lifecycle.livedata.core.ktx)
}
