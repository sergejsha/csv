import org.gradle.internal.extensions.stdlib.capitalized
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("signing")
    id("maven-publish")
    alias(libs.plugins.dokka)
    alias(libs.plugins.publish)
}

group = "de.halfbit"
version = "1.0.1"

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
        publications {
            withType<MavenPublication>().configureEach {
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
                            email.set("info@halfbit.de")
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

    }

    nexusPublishing {
        repositories {
            create("MavenCentral") {
                nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
                snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
                username.set(project.getPropertyOrEmptyString("publishing.nexus.user"))
                password.set(project.getPropertyOrEmptyString("publishing.nexus.password"))
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
}

private fun Project.getPropertyOrEmptyString(name: String): String =
    if (hasProperty(name)) property(name) as String? ?: "" else ""