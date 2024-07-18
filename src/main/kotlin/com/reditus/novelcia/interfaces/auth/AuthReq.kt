package com.reditus.novelcia.interfaces.auth

import com.reditus.novelcia.domain.user.UserCommand
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
}