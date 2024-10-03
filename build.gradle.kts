import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("maven-publish")
    id("signing")
}

group = "de.halfbit"
version = "0.13"

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()
    jvm()
    linuxX64()
    mingwX64()
    macosX64()
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

val canPublishToMaven = project.hasProperty("signing.keyId")
if (canPublishToMaven) {

    val javadocJar by tasks.registering(Jar::class) {
        archiveClassifier.set("javadoc")
    }

    publishing {

        repositories {
            maven {
                name = "local"
                url = uri("${layout.buildDirectory}/repository")
            }
            maven {
                name = "central"
                url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = project.getPropertyOrEmptyString("publishing.nexus.user")
                    password = project.getPropertyOrEmptyString("publishing.nexus.password")
                }
            }
            maven {
                name = "snapshot"
                url = uri("https://oss.sonatype.org/content/repositories/snapshots")
                credentials {
                    username = project.getPropertyOrEmptyString("publishing.nexus.user")
                    password = project.getPropertyOrEmptyString("publishing.nexus.password")
                }
            }
        }

        publications.withType<MavenPublication> {
            artifact(javadocJar.get())

            pom {
                name.set(rootProject.name)
                description.set("Tiny Kotlin Multiplatform library for building and exporting CSV files")
                url.set("https://www.halfbit.de")
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
                    url.set("https://www.halfbit.de")
                }
            }
        }

        publications.forEach { publication ->
            signing.sign(publication)
        }
    }

    signing {
        sign(publishing.publications)
    }

    // more dependencies fixes
    tasks {
        "compileTestKotlinIosSimulatorArm64" {
            mustRunAfter("signIosSimulatorArm64Publication")
        }
        "compileTestKotlinIosX64" {
            mustRunAfter("signIosX64Publication")
        }
        "compileTestKotlinIosArm64" {
            mustRunAfter("signIosArm64Publication")
        }
        "compileTestKotlinLinuxX64" {
            mustRunAfter("signLinuxX64Publication")
        }
        "compileTestKotlinMacosX64" {
            mustRunAfter("signMacosX64Publication")
        }
        "compileTestKotlinMingwX64" {
            mustRunAfter("signMingwX64Publication")
        }
    }
}

// fix for: https://github.com/gradle/gradle/issues/26091
//          https://youtrack.jetbrains.com/issue/KT-46466 is fixed
tasks.withType<AbstractPublishToMaven>().configureEach {
    dependsOn(project.tasks.withType(Sign::class.java))
}

fun Project.getPropertyOrEmptyString(name: String): String =
    if (hasProperty(name)) property(name) as String? ?: "" else ""