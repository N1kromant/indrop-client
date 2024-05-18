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
    implementation(libs.compose.preview.renderer)
    implementation(libs.kotlinx.serialization.json)

}
