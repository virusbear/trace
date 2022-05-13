plugins {
    id("java-library")
    id("maven-publish")
    id("signing")
}

repositories {
    mavenCentral()
    mavenLocal()
    google()
}

java {
    sourceCompatibility = JavaVersion.VERSION_16
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name.set("trace")
                description.set("Kotlin wrapper around the opentracing library")
                url.set("https://github.com/virusbear/trace")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("virusbear")
                        name.set("Fabian Fahrenholz")
                    }
                }

                scm {
                    connection.set("scm:https://github.com/virusbear/trace.git")
                    developerConnection.set("scm:git@github.com/virusbear/trace.git")
                    url.set("https://github.com/virusbear/trace")
                }
            }
        }
    }
}
/*
signing {
    setRequired { !project.version.toString().endsWith("-SNAPSHOT") && !project.hasProperty("skipSigning") }

    useInMemoryPgpKeys(System.getenv("GPG_SIGNING_KEY"), System.getenv("GPG_SIGNING_KEY_PASSPHRASE"))
    sign(publishing.publications["mavenJava"])
}
*/
tasks.javadoc {
    if(JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).apply {
            addBooleanOption("html5", true)
        }
    }
}