package io.github.virusbear.trace

import io.jaegertracing.Configuration
import io.opentracing.Tracer

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

    fun build(): Configuration.SamplerConfiguration =
        builder
}

class SenderBuilder {
    private val builder = Configuration.SenderConfiguration()

    fun udp(host: String, port: Int) {
        builder.withAgentHost(host)
        builder.withAgentPort(port)
    }

    fun http(endpoint: String) {
        builder.withEndpoint(endpoint)
    }

    fun build() =
        builder
}

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

    fun sender(block: SenderBuilder.() -> Unit) {
        builder.withSender(SenderBuilder().apply(block).build())
    }

    fun build(): Configuration.ReporterConfiguration =
        builder
}

class TracerBuilder(serviceName: String) {
    private val builder = Configuration(serviceName)

    fun sampler(block: SamplerBuilder.() -> Unit) {
        builder.withSampler(SamplerBuilder().apply(block).build())
    }

    fun reporter(block: ReporterBuilder.() -> Unit) {
        builder.withReporter(ReporterBuilder().apply(block).build())
    }

    fun build(): Tracer =
        builder.tracer
}

fun tracer(name: String, block: TracerBuilder.() -> Unit): Tracer =
    TracerBuilder(name).also(block).build()