import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.Base64

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.dokka)
    id("signing")
    id("maven-publish")
}

group = "de.halfbit"
version = "1.3"

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()
    jvm()
    linuxX64()
    linuxArm64()
    mingwX64()
    macosX64()
    macosArm64()
    js {
        browser {
            testTask {
                useKarma {
                    useFirefoxHeadless()
                }
            }
        }
        nodejs()
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

val canSignArtifacts = project.hasProperty("signing.keyId")
if (canSignArtifacts) {

    val javadocJar by tasks.registering(Jar::class) {
        archiveClassifier.set("javadoc")
        dependsOn(tasks.named("dokkaGeneratePublicationHtml"))
        from(layout.buildDirectory.dir("dokka/html"))
    }

    publishing {
        repositories {
            maven {
                name = "local"
                url = uri("${layout.buildDirectory}/repository")
            }
            maven {
                name = "central"
                url = uri("https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/")
                credentials {
                    username = project.getPropertyOrEmpty("publishing.nexus.user")
                    password = project.getPropertyOrEmpty("publishing.nexus.password")
                }
            }
        }

        publications.withType<MavenPublication> {
            artifact(javadocJar.get())

            pom {
                name.set(rootProject.name)
                description.set("Tiny Kotlin Multiplatform library for parsing and building CSV strings")
                url.set("https://github.com/sergejsha/${rootProject.name}")
                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("halfbit")
                        name.set("Sergej Shafarenka")
                        organization.set("Halfbit GmbH")
                        organizationUrl.set("http://www.halfbit.de")
                    }
                }
                scm {
                    connection.set("scm:git:git@github.com:sergejsha/${rootProject.name}.git")
                    developerConnection.set("scm:git:ssh://github.com:sergejsha/${rootProject.name}.git")
                    url.set("https://github.com/sergejsha/${rootProject.name}")
                }
            }
        }
    }

    signing {
        publishing.publications.withType<MavenPublication>().configureEach {
            sign(this)
        }
    }

    afterEvaluate {
        // fix for: https://github.com/gradle/gradle/issues/26091
        //          https://youtrack.jetbrains.com/issue/KT-46466 is fixed
        tasks.withType<AbstractPublishToMaven>().configureEach {
            dependsOn(project.tasks.withType(Sign::class.java))
        }
    }

    tasks.register("releaseToMavenCentral") {
        group = "publishing"
        description = "Publishes to staging and manually uploads to Maven Central"
        dependsOn("publishAllPublicationsToCentralRepository")

        doLast {
            val username = project.getPropertyOrEmpty("publishing.nexus.user")
            val password = project.getPropertyOrEmpty("publishing.nexus.password")
            val bearer = Base64.getEncoder().encodeToString("$username:$password".toByteArray())

            val request = HttpRequest.newBuilder()
                .uri(URI.create("https://ossrh-staging-api.central.sonatype.com/manual/upload/defaultRepository/de.halfbit"))
                .header("Authorization", "Bearer $bearer")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build()

            val response = HttpClient
                .newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString())

            if (response.statusCode() != 200) {
                throw GradleException("Manual upload failed ${response.statusCode()}:[${response.body()}]")
            } else {
                println(
                    "✅ Published and uploaded to Maven Central successfully." +
                            " Release to public at https://central.sonatype.com/publishing"
                )
            }
        }
    }
}

private fun Project.getPropertyOrEmpty(name: String): String =
    if (hasProperty(name)) property(name) as String? ?: "" else ""