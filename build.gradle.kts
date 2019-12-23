import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.61"
}

group = "com.dekitateserver.events"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()

    maven {
        name = "spigot"
        setUrl("https://hub.spigotmc.org/nexus/content/groups/public")
    }
}

dependencies {
    implementation("com.dekitateserver.core:DekitateCore:1.0.0-SNAPSHOT")
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
