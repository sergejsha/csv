import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithTests
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

plugins {
    kotlin("multiplatform") version "1.9.22"
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.13.2"
    id("maven-publish")
    id("signing")
}

group = "de.halfbit"
version = "0.6"

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()

    jvm {
        jvm {
            compilations.all {
                kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
            }
        }
    }
    linuxX64()
    mingwX64()
    macosX64()
    js(IR) {
        browser()
        nodejs()
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        jsTest {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        jvmTest {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
    }

    // enable running ios tests on a background thread as well
    // configuration copied from: https://github.com/square/okio/pull/929
    targets.withType<KotlinNativeTargetWithTests<*>>().all {
        binaries {
            // Configure a separate test where code runs in background
            test("background", setOf(NativeBuildType.DEBUG)) {
                freeCompilerArgs = freeCompilerArgs + "-trw"
            }
        }
        testRuns {
            val background by creating {
                setExecutionSourceFrom(binaries.getTest("background", NativeBuildType.DEBUG))
            }
        }
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
                description.set("Tiny KMP library for parsing, building and generating CSV files")
                url.set("http://www.halfbit.de")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
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
}

// fix for: https://github.com/gradle/gradle/issues/26091
//          https://youtrack.jetbrains.com/issue/KT-46466 is fixed
tasks.withType<AbstractPublishToMaven>().configureEach {
    dependsOn(project.tasks.withType(Sign::class.java))
}

// more dependencies fixes
tasks {
    "compileTestKotlinIosSimulatorArm64" {
        mustRunAfter("signIosSimulatorArm64Publication")
    }
    "compileTestKotlinIosX64" {
        mustRunAfter("signIosX64Publication")
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

fun Project.getPropertyOrEmptyString(name: String): String =
    if (hasProperty(name)) property(name) as String? ?: "" else ""