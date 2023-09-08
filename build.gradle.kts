import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.10"
    id("io.quarkus")
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.10"
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project
val ktorVersion: String by project
val kordVersion: String by project

dependencies {
    // Kotlin++!
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")

    // Quarkus
    implementation(
        enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}")
    )
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-scheduler")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-config-yaml")

    // Discord
    implementation("dev.kord:kord-core:$kordVersion")
    implementation("dev.kord.x:emoji:0.5.0")

    // HTTP
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    // Html Parser
    implementation("org.jsoup:jsoup:1.16.1")

    // MongoDB
    implementation("org.litote.kmongo:kmongo-coroutine:4.5.0")

    // Tests
    testImplementation("io.quarkus:quarkus-junit5")

    testImplementation("io.mockk:mockk:1.12.3")

    testImplementation("io.kotest:kotest-assertions-core:5.1.0")
}

group = "me.l3n.bot.discord.pensador"
version = "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "17"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "17"
}