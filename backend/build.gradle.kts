plugins {
    kotlin("jvm") version "2.2.0"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("plugin.serialization") version "2.2.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.3"
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
val mockkVersion = "1.13.8"
val junitVersion = "5.10.0"
val kotlinxCoroutinesVersion = "1.7.3"

dependencies {
    // --- Ktor core ---
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // --- Logging ---
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    // --- Database ---
    implementation("com.zaxxer:HikariCP:$hikariVersion")
    implementation("com.mysql:mysql-connector-j:$mysqlConnectorVersion")

    // --- Exposed ORM ---
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")

    // --- Kotlinx & Serialization ---
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")

    // --- Environment variables (.env) ---
    implementation("io.github.cdimascio:dotenv-kotlin:$dotenvVersion")

    // --- Security ---
    implementation("org.springframework.security:spring-security-core:6.5.5")

    // --- JWT / Authentication ---
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")

    // --- Testing ---
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinxCoroutinesVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    testImplementation(kotlin("test"))
    testImplementation("com.h2database:h2:2.2.224")
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
