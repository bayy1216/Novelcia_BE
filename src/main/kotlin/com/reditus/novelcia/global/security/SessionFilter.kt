package com.reditus.novelcia.global.security

import com.reditus.novelcia.domain.user.Role
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class SessionFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val session = request.session

        session.getLoginUserDetails()?.let {
            SecurityContextHolder.getContext().authentication = it.toAuthenticationToken()
        }

        filterChain.doFilter(request, response)
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return request.requestURI.contains("/api/auth/") // 로그인, 회원가입, 비밀번호 찾기는 필터링 하지 않음
    }
}

fun LoginUserDetails.toAuthenticationToken(): UsernamePasswordAuthenticationToken {
    return UsernamePasswordAuthenticationToken(this, null, this.authorities)
}