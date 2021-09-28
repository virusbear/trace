package io.github.virusbear.trace

import io.opentracing.Scope
import io.opentracing.Span
import io.opentracing.Tracer
import io.opentracing.util.GlobalTracer
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.ThreadContextElement
import kotlinx.coroutines.isActive
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

class CoroutineSpan(
    val tracer: Tracer = GlobalTracer.get() ?: error("No Tracer registered to GlobalTracer"),
    val span: Span? = tracer.activeSpan()
): ThreadContextElement<Scope>, AbstractCoroutineContextElement(CoroutineSpan) {
    companion object: CoroutineContext.Key<CoroutineSpan>

    override fun restoreThreadContext(context: CoroutineContext, oldState: Scope) {
        if(context.isActive) {
            span.logEvent(context, "suspend")
        }

        oldState.close()
    }

    override fun updateThreadContext(context: CoroutineContext): Scope {
        if(context.isActive) {
            span.logEvent(context, "resumed")
        }

        return tracer.activateSpan(span)
    }

    private fun Span?.logEvent(context: CoroutineContext, event: String) {
        this?.log(mapOf("event" to event, "coroutine" to (context[CoroutineName]?.name ?: "coroutine")))
    }
}