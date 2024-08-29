package com.reditus.novelcia.user.interfaces

import com.reditus.novelcia.common.domain.PositiveInt
import com.reditus.novelcia.user.domain.UserService
import com.reditus.novelcia.global.security.LoginUserDetails
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController


@Tag(name = "User")
@RestController
class UserController(
    private val userService: UserService,
) {
    @Operation(summary = "로그인 유저 정보 조회")
    @GetMapping("/api/user")
    fun getUser(
        @AuthenticationPrincipal loginUserDetails: LoginUserDetails
    ) =
        userService.getUserModelById(loginUserDetails.loginUserId.value)

    @Operation(summary = "로그인한 사용자 포인트 충전")
    @PatchMapping("/api/user/charge")
    fun chargePoint(
        @AuthenticationPrincipal loginUserDetails: LoginUserDetails,
        //멱등키
        @RequestHeader("Idempotency-Key") idempotencyKey: String,
        point: Int
    ) {
        userService.chargePoint(
            loginUserDetails.loginUserId,
            PositiveInt(point),
            idempotencyKey
        )
    }
}