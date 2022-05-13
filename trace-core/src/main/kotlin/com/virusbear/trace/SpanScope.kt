package com.virusbear.trace

import io.opentracing.Span
import io.opentracing.Tracer

class SpanScope(internal val tracer: Tracer, internal val span: Span) {
    fun tag(key: String, value: String) {
        span.setTag(key, value)
    }

    fun log(msg: String) {
        span.log(msg)
    }

    fun log(fields: Map<String, String>) {
        log(fields.map { (k, v) -> "$k=$v" }.joinToString(" "))
    }
}