package com.reditus.novelcia.global.security

import com.reditus.novelcia.user.domain.Role
import com.reditus.novelcia.user.domain.UserModel
import jakarta.servlet.http.HttpSession


fun HttpSession.getLoginUserDetails(): LoginUserDetails? {
    val userId = this.getAttribute("userId")?.toString()?.toLongOrNull()
        ?: return null
    val role = this.getAttribute("role") as String
    return LoginUserDetails(userId, Role.valueOf(role))
}



fun HttpSession.setLoginUserDetails(userModel: UserModel) {
    this.setAttribute("userId", userModel.id)
    this.setAttribute("role", userModel.role.name)
    this.maxInactiveInterval = 60 * 60 // 1시간
}