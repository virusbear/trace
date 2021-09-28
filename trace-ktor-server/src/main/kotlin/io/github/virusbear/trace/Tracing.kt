package io.github.virusbear.trace

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.util.*
import io.opentracing.Span
import io.opentracing.SpanContext
import io.opentracing.Tracer
import io.opentracing.noop.NoopTracerFactory
import io.opentracing.propagation.Format
import io.opentracing.propagation.TextMap
import io.opentracing.util.GlobalTracer

class Tracing(private val tracer: Tracer) {
    class Configuration {
        var tracer: Tracer =
            NoopTracerFactory.create()
    }

    companion object Feature: ApplicationFeature<ApplicationCallPipeline, Configuration, Tracing> {
        override val key: AttributeKey<Tracing> =
            AttributeKey("Tracing")

        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): Tracing {
            val configuration = Configuration().apply(configure)

            GlobalTracer.registerIfAbsent(configuration.tracer)

            val feature = Tracing(configuration.tracer)

            pipeline.intercept(ApplicationCallPipeline.Setup) {
                feature.tracer.withSpan(context.request.toLogString(), parent = context.request.extractSpan(feature.tracer)) {
                    proceed()
                }
            }

            pipeline.intercept(ApplicationCallPipeline.Call) {
                coroutineContext[CoroutineSpan]?.span?.let { span ->
                    feature.tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, context.response.headers.asTextMap())
                }
            }

            return feature
        }
    }
}

fun ApplicationRequest.extractSpan(tracer: Tracer): SpanContext? =
    tracer.extract(Format.Builtin.HTTP_HEADERS, headers.asTextMap())