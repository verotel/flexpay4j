import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.HttpURLConnection
import java.net.URI
import java.util.Base64

plugins {
    kotlin("jvm") version "1.9.24"
    `java-library`
    `maven-publish`
    signing
}

group = "com.verotel"
version = "2.3.0"

val sonatypeUsername = providers.gradleProperty("sonatypeUsername")
    .orElse(providers.environmentVariable("SONATYPE_USERNAME"))
val sonatypePassword = providers.gradleProperty("sonatypePassword")
    .orElse(providers.environmentVariable("SONATYPE_PASSWORD"))

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("org.assertj:assertj-core:3.22.0")
    testCompileOnly("junit:junit:4.13.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<JavaCompile> {
    targetCompatibility = "1.8"
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            pom {
                name.set("FlexPay - Verotel payment protocol library")
                description.set("Implements Verotel online payment protocol")
                url.set("https://www.verotel.com/en/integration.html")
                licenses {
                    license {
                        name.set("MIT license")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("verotel")
                        name.set("Verotel")
                        organization.set("Verotel")
                        organizationUrl.set("https://www.verotel.com")
                    }
                }
                scm {
                    connection.set("https://github.com/verotel/flexpay4j.git")
                    url.set("https://github.com/verotel/flexpay4j")
                }
            }
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "central"
            url = uri("https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/")
            credentials {
                username = sonatypeUsername.orNull
                password = sonatypePassword.orNull
            }
        }
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

signing {
    sign(publishing.publications["mavenJava"])
}

val sonatypeNamespace = "com.verotel"

val uploadCentralDeployment by tasks.registering {
    group = "publishing"
    description = "Uploads the staged deployment from the OSSRH compatibility API to Sonatype Central."

    doLast {
        val username = sonatypeUsername.orNull
            ?: error("Missing Gradle property 'sonatypeUsername'.")
        val password = sonatypePassword.orNull
            ?: error("Missing Gradle property 'sonatypePassword'.")

        val authToken = Base64.getEncoder()
            .encodeToString("$username:$password".toByteArray(Charsets.UTF_8))
        val endpoint = URI(
            "https://ossrh-staging-api.central.sonatype.com/manual/upload/defaultRepository/" +
                sonatypeNamespace +
                "?publishing_type=automatic"
        ).toURL()
        val connection = endpoint.openConnection() as HttpURLConnection

        connection.requestMethod = "POST"
        connection.setRequestProperty("Authorization", "Bearer $authToken")
        connection.setRequestProperty("Content-Type", "application/json")
        connection.doOutput = true

        connection.outputStream.use { }

        val responseCode = connection.responseCode
        if (responseCode !in 200..299) {
            val errorBody = runCatching {
                connection.errorStream?.bufferedReader()?.use { it.readText() }
            }.getOrNull()
            error(
                "Central upload handoff failed with HTTP $responseCode" +
                    if (errorBody.isNullOrBlank()) "." else ": $errorBody"
            )
        }
    }
}

tasks.named("publish") {
    finalizedBy(uploadCentralDeployment)
}
