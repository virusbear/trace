package io.github.virusbear.trace

import io.ktor.response.*
import io.ktor.util.*
import io.opentracing.propagation.TextMap

class ResponseHeaderTextMapAdapter(private val responseHeaders: ResponseHeaders): TextMap {
    override fun put(key: String, value: String) {
        responseHeaders.append(key, value)
    }

    override fun iterator(): MutableIterator<MutableMap.MutableEntry<String, String>> =
        responseHeaders.allValues()
            .toMap()
            .filterValues { it.isNotEmpty() }
            .mapValues { (_, v) -> v.first() }
            .toMutableMap().iterator()
}

fun ResponseHeaders.asTextMap(): TextMap =
    ResponseHeaderTextMapAdapter(this)