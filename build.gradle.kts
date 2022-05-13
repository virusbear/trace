import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10" apply false
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

subprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        google()
    }

    group = "io.github.virusbear.trace"
    version = "1.0.4"

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "16"
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(System.getenv("OSSRH_USERNAME"))
            password.set(System.getenv("OSSRH_PASSWORD"))
            stagingProfileId.set(System.getenv("OSSRH_STAGING_PROFILE_ID"))
        }
    }
}

val initializeSonatypeStagingRepository by tasks.existing
subprojects {
    initializeSonatypeStagingRepository {
        shouldRunAfter(tasks.withType<Sign>())
    }
}