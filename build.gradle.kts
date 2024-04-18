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
    id("java-library")
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management")
    id("com.github.ben-manes.versions")
    id("maven-publish")
}

group = "me.wilsonfranca"
version = "1.0.0"

repositories {
    mavenLocal()
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

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
        }
    }
}

/**
 * This task will create a new patch release from the current snapshot version.
 * It will update the version in the files the version is present.
 * The release version is the current version without the -SNAPSHOT suffix taken from the main branch.
 * The task will create a tag in the git repository with the release version.
 * Next, it will push the tag to the remote repository.
 * Then it will create a new patch snapshot version in the main branch
 * updating the version in the files the version is present, commit
 * and push the changes to the remote repository.
 *
 */
tasks.register("createNewPatchReleaseFromSnapshot") {
    group = "publishing"
    doLast {
        val version = currentVersion()
        val releaseVersion = version.replace("-SNAPSHOT", "")
        updateVersionInFiles(releaseVersion)
        createTagBeforeRelease(releaseVersion)
        val nextSnapshotVersion =
            "${releaseVersion.split(".")[0]}.${releaseVersion.split(".")[1]}.${releaseVersion.split(".")[2].toInt() + 1}"
        createSnapshotAfterRelease(nextSnapshotVersion)
    }
}

fun currentVersion(): String {
    return version.toString()
}

fun currentMajor(): Int {
    return currentVersion().split(".")[0].toInt()
}

fun currentMinor(): Int {
    return version.toString().split(".")[1].toInt()
}

fun currentPatch(): Int {
    return version.toString().split(".")[2].toInt()
}

fun filesToBeUpdated(): List<String> {
    return listOf("build.gradle.kts")
}

fun updateVersionInFiles(version: String) {
    filesToBeUpdated().forEach {
        val file = File(it)
        val content = file.readText()
        val updatedContent = content.replace(currentVersion(), version)
        file.writeText(updatedContent)
    }
}

fun createTagBeforeRelease(version: String, push: Boolean = false) {
    val process = ProcessBuilder("git", "tag", "-a", version, "-m", "Release $version").start()
    process.waitFor(10, TimeUnit.SECONDS)
    val exitCode = process.exitValue()
    if (exitCode != 0) {
        throw RuntimeException("Failed to create tag")
    }
    if (push) {
        val process1 = ProcessBuilder("git", "push", "origin", version).start()
        process1.waitFor(10, TimeUnit.SECONDS)
        val exitCode1 = process.exitValue()
        if (exitCode1 != 0) {
            throw RuntimeException("Failed to push tag")
        }
    }
}

fun createSnapshotAfterRelease(version: String, push: Boolean = false) {
    val nextSnapshotVersion = "${version.split(".")[0]}.${version.split(".")[1]}.${version.split(".")[2]}-SNAPSHOT"
    updateVersionInFiles(nextSnapshotVersion)
    val process2 = ProcessBuilder("git", "add", ".").start()
    process2.waitFor(10, TimeUnit.SECONDS)
    val exitCode2 = process2.exitValue()
    if (exitCode2 != 0) {
        throw RuntimeException("Failed to add files")
    }
    val process3 = ProcessBuilder("git", "commit", "-m", "New snapshot version $nextSnapshotVersion").start()
    process3.waitFor(10, TimeUnit.SECONDS)
    val exitCode3 = process3.exitValue()
    if (exitCode3 != 0) {
        throw RuntimeException("Failed to commit changes")
    }
    if (push) {
        val process4 = ProcessBuilder("git", "push", "origin", "main").start()
        process4.waitFor(10, TimeUnit.SECONDS)
        val exitCode4 = process4.exitValue()
        if (exitCode4 != 0) {
            throw RuntimeException("Failed to push changes")
        }
    }
}