package io.github.virusbear.trace

import io.opentracing.Span
import io.opentracing.Tracer
import io.opentracing.util.GlobalTracer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

suspend fun <T> Tracer.withSpan(operation: String, tags: Map<String, String> = emptyMap(), parent: Span? = null, block: suspend CoroutineScope.(SpanScope) -> T): T {
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

suspend inline fun <T> withSpan(operation: String, tags: Map<String, String> = emptyMap(), parent: Span? = null, noinline block: suspend CoroutineScope.(SpanScope) -> T): T {
    val tracer =
        coroutineContext[CoroutineSpan]?.tracer
            ?: error("No CoroutineSpan in current context. Make sure to call SpanScope.withSpan or Tracer.withSpan to ensure proper trace propagation in coroutines")

    return tracer.withSpan(operation, tags, parent, block)
}

suspend fun <T> SpanScope.withSpan(operation: String, tags: Map<String, String> = emptyMap(), block: suspend CoroutineScope.(SpanScope) -> T): T {
    return tracer.withSpan(operation, tags, span, block)
}

inline fun <T> Tracer.span(operation: String, tags: Map<String, String> = emptyMap(), parent: Span? = null, block: SpanScope.() -> T): T {
    val span = buildSpan(operation, tags, parent)

    val scope = activateSpan(span)

    try {
        return block(SpanScope(this, span))
    } finally {
        span.finish()
        scope.close()
    }
}

fun <T> SpanScope.span(operation: String, tags: Map<String, String> = emptyMap(), block: SpanScope.() -> T): T {
    return tracer.span(operation, tags, span, block)
}

inline fun <T> span(operation: String, tags: Map<String, String> = emptyMap(), block: SpanScope.() -> T): T {
    val tracer =
        GlobalTracer.get()
            ?: error("Unable to retrieve non-null tracer to create span")

    return tracer.span(operation, tags, null, block)
}