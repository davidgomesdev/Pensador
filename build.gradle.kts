import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("io.quarkus")
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project
val ktorVersion: String by project

dependencies {
    // Kotlin++!
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")

    // Quarkus
    implementation(
        enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}")
    )
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-scheduler")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-config-yaml")

    // Discord
    implementation("dev.kord:kord-core:0.7.4")
    implementation("dev.kord.x:emoji:0.5.0")

    // HTTP
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    // Html Parser
    implementation("org.jsoup:jsoup:1.10.2")

    // MongoDB
    implementation("org.litote.kmongo:kmongo-coroutine:4.4.0")

    // Tests
    testImplementation("io.quarkus:quarkus-junit5")

    testImplementation("io.mockk:mockk:1.12.1")

    testImplementation("io.kotest:kotest-assertions-core:5.0.1")
}

group = "me.l3n.bot.discord.pensador"
version = "0.0.1"

tasks.quarkusDev {
    workingDir = rootProject.projectDir.toString()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "11"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "11"
}