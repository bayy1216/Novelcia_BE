package com.reditus.novelcia.auth.interfaces

import com.reditus.novelcia.auth.domain.AuthService
import com.reditus.novelcia.user.application.UserModel
import com.reditus.novelcia.global.security.setLoginUserDetails
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpSession
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Auth", description = "인증")
@RestController
class AuthController(
    private val authService: AuthService,
) {
    @Operation(summary = "이메일 회원가입", description = "회원가입 후 자동 로그인(세션에 사용자 정보 저장)")
    @PostMapping("/api/auth/signup")
    fun signUp(
        @RequestBody req: AuthReq.EmailSignUp,
        session: HttpSession
    ): UserModel {
        authService.emailSignup(req.toCommand())
        val userModel = authService.emailSignIn(req.email, req.password)
        session.setLoginUserDetails(userModel)
        return userModel
    }

    @Operation(summary = "이메일 로그인", description = "로그인 후 세션에 사용자 정보 저장")
    @PostMapping("/api/auth/signin")
    fun signIn(
        @RequestBody req: AuthReq.EmailSignIn,
        session: HttpSession
    ): UserModel {
        val userModel = authService.emailSignIn(req.email, req.password)
        session.setLoginUserDetails(userModel)
        return userModel
    }

    @Operation(summary = "로그아웃", description = "세션 무효화")
    @PostMapping("/api/auth/signout")
    fun signOut(
        session: HttpSession,
    ) {
        session.invalidate() // 세션 무효화
    }
}