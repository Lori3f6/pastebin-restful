plugins {
    java
    kotlin("jvm") version "1.4.21"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val vertXVersion = "4.0.0"
dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.vertx:vertx-core:$vertXVersion")
    implementation("io.vertx:vertx-web:$vertXVersion")
    implementation("redis.clients:jedis:3.5.1")
    implementation("com.google.guava:guava:30.1-jre")
    testCompile("junit", "junit", "4.12")
}

tasks {

    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}