package com.reditus.novelcia.global.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.reditus.novelcia.global.controller.ApiResponse
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@EnableWebSecurity
@Configuration
@EnableMethodSecurity
class WebSecurityConfig(
    private val objectMapper: ObjectMapper,
    private val sessionFilter: SessionFilter,
) {

    @Bean
    fun configuration(): WebSecurityCustomizer {
        return WebSecurityCustomizer { webSecurity ->
            webSecurity.ignoring()
                .requestMatchers("/v3/api-docs/**")
                .requestMatchers("/swagger-ui/**")
                .requestMatchers("/h2-console/**")
        }
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf{it.disable()}
        http.sessionManagement{ it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)}
        http.formLogin{it.disable()}
        http.httpBasic{it.disable()}
        http.logout{it.disable()}

        http.cors{
            it.configurationSource(corsConfigSource())
        }

        http.addFilterBefore(sessionFilter, UsernamePasswordAuthenticationFilter::class.java)

        http.authorizeHttpRequests {
            it.requestMatchers("/api/test/**").permitAll()
            it.requestMatchers("/api/auth/**").permitAll()

            it.requestMatchers(HttpMethod.GET, "/api/tags").permitAll()

            it.requestMatchers("/api/admin/**").hasRole("ADMIN")
            it.anyRequest().authenticated()
        }




        http.exceptionHandling {
            it.authenticationEntryPoint { request, response, _ ->
                sendError(response, "UNAUTHORIZED", "인증이 필요합니다. path:${request.requestURI}",401)
            }
            it.accessDeniedHandler { _, response, _ ->
                sendError(response, "ACCESS-DENIED", "권한이 올바르지 않습니다.",403)
            }
        }




        return http.build()
    }

    private fun sendError(response: HttpServletResponse, errorCode: String, message: String, status: Int) {
        val apiError = ApiResponse.fail(message, errorCode)
        val json = objectMapper.writeValueAsString(apiError)
        response.status = status
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"
        response.writer.write(json)
        response.writer.flush()
    }




    @Bean
    fun bcryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun corsConfigSource(): CorsConfigurationSource {
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

        return source
    }

    @Bean
    fun roleHierarchy(): RoleHierarchy {
        val roleHierarchy = RoleHierarchyImpl()
        roleHierarchy.setHierarchy(
            """
                ROLE_ADMIN > ROLE_USER
                ROLE_USER > ROLE_ANONYMOUS
                """.trimIndent()
        )
        return roleHierarchy
    }

    @Bean
    fun springSessionDefaultRedisSerializer(): RedisSerializer<Any> {
        return GenericJackson2JsonRedisSerializer(objectMapper)
    }
}