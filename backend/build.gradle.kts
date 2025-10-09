plugins {
    kotlin("jvm") version "2.2.0"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("plugin.serialization") version "2.2.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val ktorVersion = "2.3.12"

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    implementation("org.jetbrains.exposed:exposed-core:0.44.1")
    testImplementation(kotlin("test"))
    // Exposed dependencies
    implementation("org.jetbrains.exposed:exposed-core:0.44.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.44.1")   // <-- DAO
    implementation("org.jetbrains.exposed:exposed-jdbc:0.44.1")  // <-- nodig voor database connectie
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.44.1")
    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${ktorVersion}")
    // Kotlin Datetime
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
    // Java time
    implementation("org.jetbrains.exposed:exposed-java-time:0.44.1")
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
