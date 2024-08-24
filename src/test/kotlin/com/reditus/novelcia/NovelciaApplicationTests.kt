package com.reditus.novelcia

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NovelciaApplicationTests @Autowired constructor(
    private val webTestClient: WebTestClient
) {

    @Test
    fun contextLoads() {
        webTestClient
            .get()
            .uri("/api/test/health")
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java).value {
                it.startsWith("UP")
            }
    }

}
