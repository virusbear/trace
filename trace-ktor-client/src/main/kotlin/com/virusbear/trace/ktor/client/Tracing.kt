package com.virusbear.trace.ktor.client

import com.virusbear.trace.CoroutineSpan
import com.virusbear.trace.asTextMap
import com.virusbear.trace.withSpan
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.util.*
import io.opentracing.propagation.Format
import io.opentracing.tag.Tags
import io.opentracing.util.GlobalTracer

class Tracing {
    class Configuration

    companion object Feature: HttpClientPlugin<Configuration, Tracing> {
        override val key: AttributeKey<Tracing> =
            AttributeKey("Tracing")

        override fun install(plugin: Tracing, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.Before) {
                withSpan(
                    context.operationName,
                    tags = mapOf(
                        Tags.SPAN_KIND.key to Tags.SPAN_KIND_CLIENT,
                        Tags.HTTP_METHOD.key to context.method.value,
                        Tags.HTTP_URL.key to context.url.buildString()
                    )
                ) {
                    proceed()
                }
            }

            scope.requestPipeline.intercept(HttpRequestPipeline.State) {
                coroutineContext[CoroutineSpan]?.span?.let {
                    GlobalTracer.get().inject(it.context(), Format.Builtin.HTTP_HEADERS, context.headers.asTextMap())
                }

                proceed()
            }
        }

        override fun prepare(block: Configuration.() -> Unit): Tracing =
            Tracing()
    }
}

private val HttpRequestBuilder.operationName: String
    get() = "${method.value} - ${url.buildString()}"