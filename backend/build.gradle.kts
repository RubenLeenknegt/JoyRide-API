plugins {
    kotlin("jvm") version "2.2.0"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("plugin.serialization") version "2.2.0"
}

group = "leafcar.backend"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val ktorVersion = "2.3.12"
val exposedVersion = "0.44.1"
val hikariVersion = "5.1.0"
val mysqlConnectorVersion = "9.1.0"
val logbackVersion = "1.5.12"
val kotlinxSerializationVersion = "1.7.3"
val kotlinxDatetimeVersion = "0.6.2"
val dotenvVersion = "6.4.1"

dependencies {
    // Ktor
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    // Database
    implementation("com.zaxxer:HikariCP:$hikariVersion")
    implementation("com.mysql:mysql-connector-j:$mysqlConnectorVersion")

    // Exposed ORM
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")

    // Serialization / Kotlin libs
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")

    // Env
    implementation("io.github.cdimascio:dotenv-kotlin:$dotenvVersion")

    // Tests
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("leafcar.backend.ApplicationKt")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

// Fat jar task
tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveFileName.set("backend-fat.jar")
    mergeServiceFiles()
}
