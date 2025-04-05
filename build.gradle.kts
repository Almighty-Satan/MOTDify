import org.jetbrains.kotlin.gradle.dsl.JvmTarget

group = "com.github.almightysatan"
version = "1.1.0"

plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
}

val lib: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

repositories {
    mavenCentral()
}

dependencies {
    lib("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.10.1")
    lib("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    lib("io.ktor:ktor-network:3.1.2")
    lib("org.apache.logging.log4j:log4j-core:2.24.3")
    testImplementation(kotlin("test"))
}

tasks.jar {
    dependsOn("copyLibs")

    manifest {
        attributes(
            Pair("Main-Class", "com.github.almightysatan.motdify.MainKt"),
            Pair("Class-Path", lib.files.joinToString(" ") { "lib-${project.version}/${it.name}" })
        )
    }
}

tasks {
    register("copyLibs", Copy::class) {
        from(lib)
        into("$buildDir/libs/lib-$version")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

tasks.test {
    useJUnitPlatform()
}
