import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
}
group = "com.ladsers.passtable"
version = "22.9.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.bouncycastle:bcpkix-jdk15on:1.66")
}

java.sourceSets["main"].java {
    srcDir("core/src/main/kotlin")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Jar>() {
    archiveFileName.set("Passtable-${project.version}.jar")

    manifest {
        attributes(
            mapOf(
                "Main-Class" to "com.ladsers.passtable.jvm.MainKt",
                "Implementation-Title" to project.name,
                "Kotlin-Version" to kotlin.coreLibrariesVersion,
                "Implementation-Version" to project.version,
                "Build-Jdk" to java.targetCompatibility
            )
        )
    }

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }
            .map { zipTree(it) }
    }) {
        exclude("META-INF/*.SF")
        exclude("META-INF/*.DSA")
        exclude("META-INF/*.RSA")
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}