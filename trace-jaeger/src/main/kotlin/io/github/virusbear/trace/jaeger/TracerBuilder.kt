package io.github.virusbear.trace.jaeger

import io.jaegertracing.Configuration
import io.opentracing.ScopeManager
import io.opentracing.Tracer

@DslMarker
annotation class JaegerTracerBuilder

@JaegerTracerBuilder
class SamplerBuilder {
    private val builder = Configuration.SamplerConfiguration()

    fun const(decision: Boolean) {
        builder.withType("const")
        builder.withParam(if(decision) 1 else 0)
    }

    fun probabilistic(probability: Double) {
        builder.withType("probabilistic")
        builder.withParam(probability)
    }

    fun ratelimiting(rateLimit: Double) {
        builder.withType("ratelimiting")
        builder.withParam(rateLimit)
    }

    fun remote() {
        builder.withType("remote")
    }

    internal fun build(): Configuration.SamplerConfiguration =
        builder
}

@JaegerTracerBuilder
class SenderBuilder {
    private val builder = Configuration.SenderConfiguration()

    fun udp(host: String, port: Int) {
        builder.withAgentHost(host)
        builder.withAgentPort(port)
    }

    fun http(endpoint: String) {
        builder.withEndpoint(endpoint)
    }

    internal fun build() =
        builder
}

@JaegerTracerBuilder
class ReporterBuilder {
    private val builder = Configuration.ReporterConfiguration()

    var flushIntervalMs: Int
        get() = builder.flushIntervalMs
        set(value) {
            builder.withFlushInterval(value)
        }

    var logSpans: Boolean
        get() = builder.logSpans
        set(value) {
            builder.withLogSpans(value)
        }

    var maxQueueSize: Int
        get() = builder.maxQueueSize
        set(value) {
            builder.withMaxQueueSize(value)
        }

    @JaegerTracerBuilder
    fun sender(block: SenderBuilder.() -> Unit) {
        builder.withSender(SenderBuilder().apply(block).build())
    }

    internal fun build(): Configuration.ReporterConfiguration =
        builder
}

@JaegerTracerBuilder
class TracerBuilder(serviceName: String) {
    private val builder = Configuration(serviceName)
    private var scopeManagerFactory: (() -> ScopeManager)? = null

    @JaegerTracerBuilder
    fun sampler(block: SamplerBuilder.() -> Unit) {
        builder.withSampler(SamplerBuilder().apply(block).build())
    }

    @JaegerTracerBuilder
    fun reporter(block: ReporterBuilder.() -> Unit) {
        builder.withReporter(ReporterBuilder().apply(block).build())
    }

    @JaegerTracerBuilder
    fun scopeManager(block: () -> ScopeManager) {
        scopeManagerFactory = block
    }

    internal fun build(): Tracer =
        builder.tracerBuilder.let { builder ->
            scopeManagerFactory?.let { factory ->
                builder.withScopeManager(factory())
            } ?: builder
        }.build()
}

@JaegerTracerBuilder
fun tracer(name: String, block: TracerBuilder.() -> Unit): Tracer =
    TracerBuilder(name).also(block).build()