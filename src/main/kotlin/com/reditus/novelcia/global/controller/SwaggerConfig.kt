package com.reditus.novelcia.global.controller

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile


@Configuration
class SwaggerConfig {
    @Bean
    fun openAPI(
        server: Server,
    ): OpenAPI = OpenAPI()
        .info(apiInfo())
        .servers(listOf(server))

    private fun apiInfo() = Info()
        .title("Novelcia API")
        .description("api 문서입니다.")
        .version("0.0.1")

    @Bean
    fun getLocalServer(): Server = Server()
        .url("http://localhost:8080")
        .description("Local Server")

    @Bean
    @Primary
    @Profile("prod") // prod 환경에서만 사용되어 Primary가 활성화 됨
    fun getProdServer(): Server = Server()
        .url("https://c2c.reditus.site")
        .description("Production Server")
}