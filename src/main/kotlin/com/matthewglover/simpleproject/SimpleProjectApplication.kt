package com.matthewglover.simpleproject

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SimpleProjectApplication

fun main(args: Array<String>) {

    @Suppress("SpreadOperator")
    runApplication<SimpleProjectApplication>(*args)
}
