package io.github.virusbear.trace.ktor.server

import io.github.virusbear.trace.asTextMap
import io.github.virusbear.trace.withSpan
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.request.*
import io.ktor.util.*
import io.opentracing.SpanContext
import io.opentracing.Tracer
import io.opentracing.propagation.Format
import io.opentracing.tag.Tags
import io.opentracing.util.GlobalTracer

class Tracing {
    class Configuration

    companion object Feature: ApplicationFeature<ApplicationCallPipeline, Configuration, Tracing> {
        override val key: AttributeKey<Tracing> =
            AttributeKey("Tracing")

        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): Tracing {
            val feature = Tracing()

            pipeline.intercept(ApplicationCallPipeline.Setup) {
                withSpan(
                    context.request.toLogString(),
                    tags = mapOf(
                        Tags.SPAN_KIND.key to Tags.SPAN_KIND_SERVER,
                        Tags.HTTP_METHOD.key to context.request.httpMethod.value
                    ),
                    parent = context.request.extractSpan(GlobalTracer.get())
                ) {
                    proceed()
                }
            }

            return feature
        }
    }
}

fun ApplicationRequest.extractSpan(tracer: Tracer): SpanContext? =
    tracer.extract(Format.Builtin.HTTP_HEADERS, headers.asTextMap())