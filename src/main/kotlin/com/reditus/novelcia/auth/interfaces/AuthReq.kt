package com.reditus.novelcia.auth.interfaces

import com.reditus.novelcia.user.domain.UserCommand
import io.swagger.v3.oas.annotations.media.Schema

class AuthReq {
    data class EmailSignUp(
        @Schema(example = "test@a.c")
        val email: String,
        @Schema(example = "test")
        val password: String,
        val nickname: String,
    ){
        fun toCommand() = UserCommand.Create(
            email = email,
            password = password,
            nickname = nickname,
        )
    }

    data class EmailSignIn(
        @Schema(example = "test@a.c")
        val email: String,
        @Schema(example = "test")
        val password: String,
    )
}