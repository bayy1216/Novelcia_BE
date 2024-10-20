package com.reditus.novelcia.global.security

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Component
class WebCorsConfigurationSource: CorsConfigurationSource {
    override fun getCorsConfiguration(request: HttpServletRequest): CorsConfiguration? {
        val configuration = CorsConfiguration()
        configuration.setAllowedOriginPatterns(listOf("*")) // 모든 요청 허용
        configuration.allowedMethods = listOf(
            "GET", "POST", "PUT", "DELETE", "PATCH",
            "OPTIONS"
        ) // 모든 HTTP 메서드 허용
        configuration.allowedHeaders = listOf("*") // 모든 헤더 허용
        configuration.exposedHeaders = listOf("Authorization", "Set-cookie")
        configuration.allowCredentials = true // 쿠키와 같은 자격 증명을 허용

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)

        return configuration
    }
}