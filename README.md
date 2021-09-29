# trace
[![Scan with Detekt](https://github.com/virusbear/trace/actions/workflows/detekt-analysis.yml/badge.svg?branch=main)](https://github.com/virusbear/trace/actions/workflows/detekt-analysis.yml)
[![Build](https://github.com/virusbear/trace/actions/workflows/gradle_build.yml/badge.svg?branch=main)](https://github.com/virusbear/trace/actions/workflows/gradle_build.yml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin](https://img.shields.io/badge/kotlin-1.5.30-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.virusbear.trace/trace-core)](https://mvnrepository.com/artifact/io.github.virusbear.trace)

## trace-core
trace is a lightweight kotlin dsl wrapper around the OpenTracing api.
both normal blocking code as well as kotlin coroutines are supported.

#### Installation
```kotlin
implementation("io.github.virusbear.trace:trace-core:$traceVersion")
```

#### Usage
`GlobalTracer` defined in opentracing-util is used to get access to a tracer when using the coroutine api.
Make sure to register a `Tracer` instance to the `GlobalTracer` to ensure proper propagation of this instance.

```kotlin
//Blocking
span("your-operation", tags = mapOf("optional" to "tags"), parent = optionalParentSpanContext) {
    //Code to be traced
}

//Coroutines
withSpan("your-operation", tags = mapOf("optional" to "tags"), parent = optionalParentSpanContext) {
  //Suspending code to be traced
}
```

#### SpanScope
Every `span` or `withSpan` function receives a `SpanScope` for the tracing lambda. 
SpanScope provides a way to add tags to a given span or log certain events.

```kotlin
//span receives SpanScope as receiver for lambda.
span("spanScope") {
  tag("your-tag", "tag-value")
  log(msg)
}

//withSpan receives SpanScope as parameter for lambda. Receiver will be CoroutineScope
span("spanScope") { scope ->
  scope.tag("your-tag", "tag-value")
  scope.log(msg)
}
```

## trace client builder dsl
Currently trace provides a typesafe builder dsl for creating new instances of the jaeger tracing client using the official JaegerTracing java client library.

It is planned to implement builders for more OpenTracing client implementations in the future


#### Installation
```kotlin
implementation("io.github.virusbear.trace:trace-jaeger:$traceVersion")
```

#### Usage
```kotlin
val tracer: Tracer = tracer("tracerName") {
    sampler {
        //set properties for sampler
    }
    reporter {
        //set properties for reporter
        sender {
            //set properties for sender
        }
    }
    scopeManager {
        customScopeManager
    }
}
```

## trace ktor
trace provides Features for both ktor-client and ktor-server.
Just call `install(Tracing)` when setting up the server or client with the corresponding Feature implementation. Trace takes care of the rest

#### Installation
```kotlin
implementation("io.github.virusbear.trace:trace-ktor-server:$traceVersion")
implementation("io.github.virusbear.trace:trace-ktor-client:$traceVersion")
```


