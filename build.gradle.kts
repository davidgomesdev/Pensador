import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.20"
    id("io.quarkus")
    id("org.jetbrains.kotlin.plugin.allopen") version "1.6.20"
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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")

    // Quarkus
    implementation(
        enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}")
    )
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-scheduler")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-config-yaml")

    // Discord
    implementation("dev.kord:kord-core:0.8.0-M12")
    implementation("dev.kord.x:emoji:0.5.0")

    // HTTP
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    // Html Parser
    implementation("org.jsoup:jsoup:1.14.3")

    // MongoDB
    implementation("org.litote.kmongo:kmongo-coroutine:4.5.1")

    // Tests
    testImplementation("io.quarkus:quarkus-junit5")

    testImplementation("io.mockk:mockk:1.12.3")

    testImplementation("io.kotest:kotest-assertions-core:5.3.0")
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