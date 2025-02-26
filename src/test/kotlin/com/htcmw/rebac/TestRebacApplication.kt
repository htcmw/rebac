package com.htcmw.rebac

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<RebacApplication>().with(TestcontainersConfiguration::class).run(*args)
}
