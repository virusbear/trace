package io.github.virusbear.trace

import io.opentracing.SpanContext
import io.opentracing.Tracer
import io.opentracing.noop.NoopTracerFactory
import io.opentracing.util.GlobalTracer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

internal suspend inline fun <T> Tracer.withSpan(
    operation: String,
    tags: Map<String, String> = emptyMap(),
    parent: SpanContext? = null,
    crossinline block: suspend CoroutineScope.(SpanScope) -> T
): T {
    val span = buildSpan(operation, tags, parent)

    try {
        val context = if(coroutineContext[CoroutineSpan]?.span != span) {
            coroutineContext + CoroutineSpan(this, span)
        } else {
            coroutineContext
        }

        return withContext(context) {
            block(SpanScope(this@withSpan, span))
        }
    } finally {
        span.finish()
    }
}

suspend fun <T> withSpan(
    operation: String,
    tags: Map<String, String> = emptyMap(),
    parent: SpanContext? = null,
    block: suspend CoroutineScope.(SpanScope) -> T
): T {
    val tracer =
        coroutineContext[CoroutineSpan]?.tracer
            ?: GlobalTracer.get()
            ?: NoopTracerFactory.create()

    return tracer.withSpan(operation, tags, parent ?: coroutineContext[CoroutineSpan]?.span?.context(), block)
}

suspend fun <T> SpanScope.withSpan(
    operation: String,
    tags: Map<String, String> = emptyMap(),
    block: suspend CoroutineScope.(SpanScope) -> T
): T {
    return tracer.withSpan(operation, tags, span.context(), block)
}

inline fun <T> Tracer.span(
    operation: String,
    tags: Map<String, String> = emptyMap(),
    parent: SpanContext? = null,
    block: SpanScope.() -> T
): T {
    val span = buildSpan(operation, tags, parent)

    val scope = activateSpan(span)

    try {
        return block(SpanScope(this, span))
    } finally {
        span.finish()
        scope.close()
    }
}

fun <T> SpanScope.span(
    operation: String,
    tags: Map<String, String> = emptyMap(),
    block: SpanScope.() -> T
): T {
    return tracer.span(operation, tags, span.context(), block)
}

inline fun <T> span(
    operation: String,
    tags: Map<String, String> = emptyMap(),
    block: SpanScope.() -> T
): T {
    val tracer =
        GlobalTracer.get()
            ?: NoopTracerFactory.create()

    return tracer.span(operation, tags, tracer.activeSpan()?.context(), block)
}