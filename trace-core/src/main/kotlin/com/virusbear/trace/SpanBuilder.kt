package com.virusbear.trace

import io.opentracing.Span
import io.opentracing.SpanContext
import io.opentracing.Tracer

fun Tracer.buildSpan(operation: String, tags: Map<String, String>, parent: SpanContext? = null): Span =
    buildSpan(operation) {
        withTags(tags).run {
            parent?.let {
                asChildOf(it)
            } ?: this
        }
    }

fun Tracer.SpanBuilder.withTags(tags: Map<String, String>): Tracer.SpanBuilder =
    tags.toList().fold(this) { builder, (key, value) ->
        builder.withTag(key, value)
    }

fun Tracer.buildSpan(operation: String, builder: Tracer.SpanBuilder.() -> Tracer.SpanBuilder = { this }): Span =
    buildSpan(operation).builder().start()