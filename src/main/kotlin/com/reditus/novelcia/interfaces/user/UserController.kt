package com.reditus.novelcia.interfaces.user

import com.reditus.novelcia.domain.LoginUserId
import com.reditus.novelcia.domain.PositiveInt
import com.reditus.novelcia.domain.user.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val userService: UserService,
) {
    @GetMapping("/api/user")
    fun getUser() =
        userService.getUserModelById(1)

    @PostMapping("/api/user/charge")
    fun chargePoint() {
        userService.chargePoint(
            LoginUserId(1),
            PositiveInt(1000)
        )
    }
}