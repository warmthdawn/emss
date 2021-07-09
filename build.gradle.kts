val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val koin_version: String by project
val docker_java_version: String by project
val ktorm_version: String by project
val ebean_version: String by project
val h2_version: String by project

plugins {
    application
    kotlin("jvm") version "1.5.20"
    id("io.ebean") version "12.9.1"
    kotlin("kapt") version "1.5.20-RC"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.20"
}

group = "top.warmthdawn.emss"
version = "0.0.1"
application {
    mainClass.set("top.warmthdawn.emss.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-auth:$ktor_version")
    implementation("io.ktor:ktor-auth-jwt:$ktor_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("io.ktor:ktor-websockets:$ktor_version")
    implementation("io.ktor:ktor-server-tomcat:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    // Koin
    implementation("io.insert-koin:koin-ktor:$koin_version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")
    // Docker-Java
    implementation("com.github.docker-java:docker-java-core:$docker_java_version")
    implementation("com.github.docker-java:docker-java-transport-httpclient5:$docker_java_version")

    // Ktorm
    implementation("org.ktorm:ktorm-core:$ktorm_version")
    implementation("io.ebean:ebean:$ebean_version")
    kapt("io.ebean:kotlin-querybean-generator:$ebean_version")
    implementation("com.h2database:h2:$h2_version")
    // Testing
    testImplementation("io.ktor:ktor-server-test-host:1.6.1")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("io.insert-koin:koin-test:$koin_version")
    testImplementation("junit:junit:4.12")
    testImplementation("io.ebean:ebean-test:$ebean_version")
}