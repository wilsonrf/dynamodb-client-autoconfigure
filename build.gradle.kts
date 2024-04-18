/*
* Copyright 2024 Wilson da Rocha França
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
 */
plugins {
    id("java")
    id("java-library")
    id("org.springframework.boot") version "3.2.4"
    id("io.spring.dependency-management") version "1.1.3"
    id("com.github.ben-manes.versions") version "0.51.0"
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management")
    id("com.github.ben-manes.versions")
    id("maven-publish")
}

group = "me.wilsonfranca"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val awsSdkVersion: String by project

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
        mavenBom("software.amazon.awssdk:bom:$awsSdkVersion")
    }
}
dependencies {
    api("org.testcontainers:testcontainers")
    api("org.springframework.boot:spring-boot-testcontainers")
    api("org.springframework.boot:spring-boot-starter")
    api("software.amazon.awssdk:dynamodb")
    api("software.amazon.awssdk:dynamodb-enhanced")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}