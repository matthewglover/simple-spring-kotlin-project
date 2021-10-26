package com.matthewglover.simpleproject

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.matthewglover.simpleproject"])
class SimpleProjectApplication

fun main(args: Array<String>) {

    @Suppress("SpreadOperator")
    runApplication<SimpleProjectApplication>(*args)
}
