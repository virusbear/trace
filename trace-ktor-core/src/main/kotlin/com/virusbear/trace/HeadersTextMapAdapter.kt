package com.virusbear.trace

import io.ktor.http.*
import io.ktor.util.*
import io.opentracing.propagation.TextMap

class HeadersTextMapAdapter(private val headers: Headers): TextMap {
    override fun put(key: String?, value: String?) {
        error("${this::class.simpleName} is read only.")
    }

    override fun iterator(): MutableIterator<MutableMap.MutableEntry<String, String>> =
        headers
            .toMap()
            .filterValues { it.isNotEmpty() }
            .mapValues { (_, v) -> v.first() }
            .toMutableMap().iterator()
}

fun Headers.asTextMap(): HeadersTextMapAdapter =
    HeadersTextMapAdapter(this)