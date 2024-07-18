package com.reditus.novelcia.interfaces.user

import com.reditus.novelcia.domain.LoginUserId
import com.reditus.novelcia.domain.PositiveInt
import com.reditus.novelcia.domain.user.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


@Tag(name = "User")
@RestController
class UserController(
    private val userService: UserService,
) {
    @Operation(summary = "로그인 유저 정보 조회")
    @GetMapping("/api/user")
    fun getUser() =
        userService.getUserModelById(1)

    @Operation(summary = "로그인한 사용자 포인트 충전")
    @PostMapping("/api/user/charge")
    fun chargePoint() {
        userService.chargePoint(
            LoginUserId(1),
            PositiveInt(1000)
        )
    }
}