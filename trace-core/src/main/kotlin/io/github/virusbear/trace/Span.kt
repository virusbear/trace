package io.github.virusbear.trace

import io.opentracing.SpanContext
import io.opentracing.Tracer
import io.opentracing.util.GlobalTracer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

suspend fun <T> Tracer.withSpan(operation: String, tags: Map<String, String> = emptyMap(), parent: SpanContext? = null, builder: Tracer.SpanBuilder.() -> Unit = {}, block: suspend CoroutineScope.(SpanScope) -> T): T {
    val span = buildSpan(operation, tags, parent, builder)

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

suspend inline fun <T> withSpan(operation: String, tags: Map<String, String> = emptyMap(), parent: SpanContext? = null, noinline builder: Tracer.SpanBuilder.() -> Unit = {}, noinline block: suspend CoroutineScope.(SpanScope) -> T): T {
    val tracer =
        coroutineContext[CoroutineSpan]?.tracer
            ?: error("No CoroutineSpan in current context. Make sure to call SpanScope.withSpan or Tracer.withSpan to ensure proper trace propagation in coroutines")

    return tracer.withSpan(operation, tags, parent, builder, block)
}

suspend fun <T> SpanScope.withSpan(operation: String, tags: Map<String, String> = emptyMap(), builder: Tracer.SpanBuilder.() -> Unit = {}, block: suspend CoroutineScope.(SpanScope) -> T): T {
    return tracer.withSpan(operation, tags, span.context(), builder, block)
}

inline fun <T> Tracer.span(operation: String, tags: Map<String, String> = emptyMap(), parent: SpanContext? = null, noinline builder: Tracer.SpanBuilder.() -> Unit = {}, block: SpanScope.() -> T): T {
    val span = buildSpan(operation, tags, parent, builder)

    val scope = activateSpan(span)

    try {
        return block(SpanScope(this, span))
    } finally {
        span.finish()
        scope.close()
    }
}

fun <T> SpanScope.span(operation: String, tags: Map<String, String> = emptyMap(), builder: Tracer.SpanBuilder.() -> Unit = {}, block: SpanScope.() -> T): T {
    return tracer.span(operation, tags, span.context(), builder, block)
}

inline fun <T> span(operation: String, tags: Map<String, String> = emptyMap(), noinline builder: Tracer.SpanBuilder.() -> Unit = {}, block: SpanScope.() -> T): T {
    val tracer =
        GlobalTracer.get()
            ?: error("Unable to retrieve non-null tracer to create span")

    return tracer.span(operation, tags, null, builder, block)
}