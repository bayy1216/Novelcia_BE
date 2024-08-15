package com.reditus.novelcia.interfaces

import org.springframework.core.env.Environment
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController(
    private val environment: Environment
) {
    @GetMapping("/api/test/health")
    fun profileHealth(): String {
        val hostName = environment.getProperty("HOSTNAME")
        return "UP ${environment.activeProfiles.joinToString()}:$hostName"
    }
}