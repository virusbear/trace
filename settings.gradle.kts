pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}

rootProject.name = "trace"
include("trace-core")
include("trace-ktor-server")
include("trace-jaeger")
include("trace-ktor-core")
include("trace-ktor-client")
