package io.github.virusbear.trace

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.*
import io.opentracing.Span
import io.opentracing.SpanContext
import io.opentracing.Tracer
import io.opentracing.noop.NoopTracerFactory
import io.opentracing.propagation.Format

class Tracing(private val tracer: Tracer) {
    class Configuration {
        var tracer: Tracer =
            NoopTracerFactory.create()
    }

    companion object Feature: HttpClientFeature<Configuration, Tracing> {
        override val key: AttributeKey<Tracing> =
            AttributeKey("Tracing")

        private val spanKey: AttributeKey<Span> =
            AttributeKey("Tracing-Span")

        override fun install(feature: Tracing, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.Before) {
                feature.tracer.withSpan(context.operationName) {
                    proceed()
                }
            }

            scope.requestPipeline.intercept(HttpRequestPipeline.State) {
                coroutineContext[CoroutineSpan]?.span?.let {
                    feature.tracer.inject(it.context(), Format.Builtin.HTTP_HEADERS, context.headers.asTextMap())
                    context.attributes.put(spanKey, it)
                }

                proceed()
            }

            scope.receivePipeline.intercept(HttpReceivePipeline.Before) {
                feature.tracer.withSpan(
                    operation = context.request.operationName,
                    parent = context.request.extractSpan(feature.tracer),
                    builder = {
                        asChildOf(context.attributes.getOrNull(spanKey)?.context())
                    }
                ) {
                    proceed()
                }
            }
        }

        override fun prepare(block: Configuration.() -> Unit): Tracing =
            Tracing(
                Configuration().apply(block).tracer
            )
    }
}

private val HttpRequestBuilder.operationName: String
    get() = "$method - $url"

private val HttpRequest.operationName: String
    get() = "$method - $url"

fun HttpRequest.extractSpan(tracer: Tracer): SpanContext? =
    tracer.extract(Format.Builtin.HTTP_HEADERS, headers.asTextMap())