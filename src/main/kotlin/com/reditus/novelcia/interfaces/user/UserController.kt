package com.reditus.novelcia.interfaces.user

import com.reditus.novelcia.domain.PositiveInt
import com.reditus.novelcia.domain.user.UserService
import com.reditus.novelcia.global.security.LoginUserDetails
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
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
    fun getUser(
        @AuthenticationPrincipal loginUserDetails: LoginUserDetails
    ) =
        userService.getUserModelById(loginUserDetails.loginUserId.value)

    @Operation(summary = "로그인한 사용자 포인트 충전")
    @PostMapping("/api/user/charge")
    fun chargePoint(
        @AuthenticationPrincipal loginUserDetails: LoginUserDetails,
        point: Int
    ) {
        userService.chargePoint(
            loginUserDetails.loginUserId,
            PositiveInt(point)
        )
    }
}