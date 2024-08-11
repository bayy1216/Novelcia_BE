package com.reditus.novelcia.global.controller

import com.reditus.novelcia.global.security.LoginUserDetails
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.util.*

@Configuration
class WebConfig(
    private val commonHttpRequestInterceptor: CommonHttpRequestInterceptor
): WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(commonHttpRequestInterceptor)
    }
}

@Component
class CommonHttpRequestInterceptor : HandlerInterceptor{
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        var requestId = request.getHeader(HEADER_REQUEST_UUID_KEY)
        if(requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString()
        }
        MDC.put(HEADER_REQUEST_UUID_KEY, requestId)
        return super.preHandle(request, response, handler)
    }

    companion object{
        const val HEADER_REQUEST_UUID_KEY = "x-request-id"
    }
}