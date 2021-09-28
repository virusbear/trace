package io.github.virusbear.trace

import io.ktor.http.*
import io.ktor.util.*
import io.opentracing.propagation.TextMap

class HeadersBuilderTextMapAdapter(private val headersBuilder: HeadersBuilder): TextMap {
    override fun put(key: String, value: String) {
        headersBuilder[key] = value
    }

    override fun iterator(): MutableIterator<MutableMap.MutableEntry<String, String>> =
        headersBuilder.build()
            .toMap()
            .filterValues { it.isNotEmpty() }
            .mapValues { (_, v) -> v.first() }
            .toMutableMap().iterator()
}

fun HeadersBuilder.asTextMap(): HeadersBuilderTextMapAdapter =
    HeadersBuilderTextMapAdapter(this)