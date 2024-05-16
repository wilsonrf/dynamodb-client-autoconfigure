

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
    id("base")
    id("java-library")
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management")
    id("com.github.ben-manes.versions")
    id("org.owasp.dependencycheck")
    id("maven-publish")
    id("signing")
    id("jacoco")
}

group = "com.wilsonfranca"

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

    implementation("org.springframework.boot:spring-boot-starter")
    api("software.amazon.awssdk:dynamodb")
    api("software.amazon.awssdk:dynamodb-enhanced")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.springframework.boot:spring-boot-autoconfigure-processor")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
    testLogging { exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL }
    finalizedBy("jacocoTestReport")
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

tasks.register<Jar>("javadocJar") {
    dependsOn("javadoc")
    archiveClassifier.set("javadoc")
    from(tasks["javadoc"].outputs)
}

tasks.register("writePatchSnapshotVersion") {
    group = "publishing"
    dependsOn("build")
    doLast {
        if (!isMainBranch()) {
            println("Not in main branch. Skipping patch snapshot version.")
            return@doLast
        }
        println("Writing patch snapshot version")
        val version = currentVersion()
        println("Current version is $version")
        val nextSnapshotVersion = "${currentMajor()}.${currentMinor()}.${currentPatch() + 1}-SNAPSHOT"
        println("Next snapshot version is $nextSnapshotVersion")
        updateVersionInFiles(nextSnapshotVersion)
    }
}

val zipArtifacts by tasks.registering(Zip::class) {
    dependsOn("publishMavenJavaPublicationToInternalRepoRepository")
    from("${layout.buildDirectory.get()}/repo") {
        exclude("**/maven-metadata*.*")
    }
    archiveFileName.set("${project.name}-${version}.zip")
    destinationDirectory.set(file("${layout.buildDirectory.get()}/outputs"))
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])

            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }

            pom {
                name = "DynamoDB Client Autoconfigure"
                description = "DynamoDB Client Autoconfigure"
                url = "https://github.com/wilsonrf/dynamodb-client-autoconfigure"
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "wilsonrf"
                        name = "Wilson da Rocha França"
                        email = "wilsonrf@gmail.com"
                    }
                }
                scm {
                    connection = "scm:git:git@github.com:wilsonrf/dynamodb-client-autoconfigure.git"
                    developerConnection = "scm:git:git@github.com:wilsonrf/dynamodb-client-autoconfigure.git"
                    url = "https://github.com/wilsonrf/dynamodb-client-autoconfigure"
                }
            }
        }

        repositories {

            maven {
                name = "internalRepo"
                url = uri("${layout.buildDirectory.get()}/repo")
            }

            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/wilsonrf/dynamodb-client-autoconfigure")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}

signing {
    setRequired {
        gradle.taskGraph.allTasks.any { it is PublishToMavenLocal }.not()
    }

    val signingKey: String? by project
    val signingKeyId: String? by project
    val signingPassword: String? by project

    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)

    sign(publishing.publications["mavenJava"])
}

/**
 * This function returns the current version of the project.
 */
fun currentVersion(): String {
    return version.toString()
}

/**
 * This function returns the current major version of the project.
 */
fun currentMajor(): Int {
    return currentVersion().split(".")[0].toInt()
}

/**
 * This function returns the current minor version of the project.
 */
fun currentMinor(): Int {
    return version.toString().split(".")[1].toInt()
}

/**
 * This function returns the current patch version of the project.
 */
fun currentPatch(): Int {
    return version.toString().split(".")[2].replace("-SNAPSHOT", "").toInt()
}

/**
 * This function returns the files that need to be updated with the new version.
 */
fun filesToBeUpdated(): List<String> {
    return listOf("gradle.properties", "README.adoc")
}

/**
 * This function updates the version in the files that need to be updated.
 */
fun updateVersionInFiles(version: String) {
    filesToBeUpdated().forEach {
        val file = File(it)
        val content = file.readText()
        val currentVersion = currentVersion()
        println("Updating version from $currentVersion to $version in file $it")
        val updatedContent = content.replace(currentVersion, version)
        file.writeText(updatedContent)
    }
}

/**
 * This function checks if this is the current main branch.
 */
fun isMainBranch(): Boolean {
    val gitCommand = "git rev-parse --abbrev-ref HEAD"
    val process = ProcessBuilder(gitCommand.split(" ")).start()
    process.waitFor(10, TimeUnit.SECONDS)
    val exitCode = process.exitValue()
    if (exitCode != 0) {
        throw RuntimeException("Failed get current branch")
    }
    val currentBranch = process.inputStream.bufferedReader().readText().trim()
    println("Current branch is $currentBranch")
    return currentBranch == "main"
}