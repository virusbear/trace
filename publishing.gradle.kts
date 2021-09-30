import java.net.URI

apply(plugin = "maven-publish")
apply(plugin = "java-library")
apply(plugin = "signing")

tasks {
    val sourcesJar by creating(Jar::class) {
        archiveClassifier.set("sources")

        from(project.the<SourceSetContainer>().getByName("main").allSource)
    }
    val javadocJar by creating(Jar::class) {
        val javadoc by getting
        dependsOn.add(javadoc)
        archiveClassifier.set("javadoc")
        from(javadoc)
    }

    val publish by getting
    val build by getting

    publish.dependsOn.add(build)

    fun ArtifactHandler.archives(artifactNotation: Any): PublishArtifact =
        add("archives", artifactNotation)

    artifacts {
        val jar by getting
        archives(sourcesJar)
        archives(javadocJar)
        archives(jar)
    }
}

afterEvaluate {
    configure<SigningExtension> {
        useInMemoryPgpKeys(System.getenv("GPG_SIGNING_KEY"), System.getenv("GPG_SIGNING_KEY_PASSPHRASE"))
        sign(project.the<PublishingExtension>().publications)
    }

    configure<PublishingExtension> {
        repositories {
            maven {
                val releasesRepoUrl = URI("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                val snapshotsRepoUrl = URI("https://s01.oss.sonatype.org/content/repositories/snapshots")
                url = if("${rootProject.version}".endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

                credentials {
                    username = System.getenv("OSSRH_USERNAME")
                    password = System.getenv("OSSRH_PASSWORD")
                }
            }
        }

        publications {
            create<MavenPublication>("release") {
                groupId = extra["groupId"]?.toString()
                artifactId = extra["artifactId"]?.toString()
                version = extra["version"]?.toString()

                artifact(project.tasks.getByName("sourcesJar"))
                artifact(project.tasks.getByName("javadocJar"))
                artifact(project.tasks.getByName("jar"))

                pom {
                    name.set(artifactId)
                    description.set(extra["description"]?.toString())
                    url.set("https://github.com/virusbear/trace")
                    licenses {
                        license {
                            name.set("Apache-2.0 License")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0")
                        }
                    }
                    developers {
                        developer {
                            id.set("virusbear")
                        }
                    }
                    scm {
                        url.set("https://github.com/virusbear/trace")
                    }
                    withXml {
                        val dependencies = asNode().appendNode("dependencies")
                        project.configurations.getByName("api").allDependencies.forEach {
                            val dependency = dependencies.appendNode("dependency")
                            dependency.appendNode("groupId", it.group)
                            dependency.appendNode("artifactId", it.name)
                            dependency.appendNode("version", it.version)
                            dependency.appendNode("scope", "compile")
                        }
                    }
                }
            }
        }
    }
}