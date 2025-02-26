package com.htcmw.rebac

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RebacApplication

fun main(args: Array<String>) {
    runApplication<RebacApplication>(*args)
}
