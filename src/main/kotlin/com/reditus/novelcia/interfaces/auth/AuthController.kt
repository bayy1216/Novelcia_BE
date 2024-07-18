package com.reditus.novelcia.interfaces.auth

import com.reditus.novelcia.domain.auth.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Auth")
@RestController
class AuthController(
    private val authService: AuthService,
) {
    @Operation(summary = "이메일 회원가입")
    @PostMapping("/api/auth/signup")
    fun signUp(
        @RequestBody req: AuthReq.EmailSignUp,
    ) {
        authService.emailSignup(req.toCommand())
    }
}