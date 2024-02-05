import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

plugins {
    kotlin("jvm") version "1.6.20"
    `java-library`
    `maven-publish`
    signing
}

group = "com.verotel"
version = "1.2.1"

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
            url = URI("https://s01.oss.sonatype.org/content/repositories/releases/")
            credentials(PasswordCredentials::class.java)
        }
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

signing {
    sign(configurations.archives.get())
    sign(publishing.publications["mavenJava"])
}
