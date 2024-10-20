package com.reditus.novelcia.global.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.reditus.novelcia.global.controller.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class JsonEntryPoint(
    private val objectMapper: ObjectMapper,
) : AuthenticationEntryPoint, AccessDeniedHandler {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException,
    ) {
        val apiError = ApiResponse.fail("인증이 필요합니다. path: ${request.requestURI}", "UNAUTHORIZED")
        val json = objectMapper.writeValueAsString(apiError)
        response.sendJson(json, 401)
    }

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException,
    ) {
        val apiError = ApiResponse.fail("인증이 필요합니다. path: ${request.requestURI}", "UNAUTHORIZED")
        val json = objectMapper.writeValueAsString(apiError)
        response.sendJson(json, 401)
    }
}

fun HttpServletResponse.sendJson(json: String, statusCode: Int) {
    status = statusCode
    contentType = MediaType.APPLICATION_JSON_VALUE
    characterEncoding = "UTF-8"
    writer.write(json)
    writer.flush()
}