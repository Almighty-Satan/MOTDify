import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.github.almightysatan"
version = "1.0.0"

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
}

val lib: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

repositories {
    mavenCentral()
}

dependencies {
    lib("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.4")
    lib("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
    lib("io.ktor:ktor-network:2.1.1")
    lib("org.apache.logging.log4j:log4j-core:2.19.0")
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

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.test {
    useJUnitPlatform()
}
