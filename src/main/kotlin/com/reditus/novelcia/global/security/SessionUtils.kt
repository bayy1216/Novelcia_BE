package com.reditus.novelcia.global.security

import com.reditus.novelcia.domain.user.Role
import com.reditus.novelcia.domain.user.UserModel
import jakarta.servlet.http.HttpSession


fun HttpSession.getLoginUserDetails(): LoginUserDetails? {
    val userId = this.getAttribute("userId") as Long?
    val role = this.getAttribute("role") as String?
    return if (userId != null && role != null) {
        LoginUserDetails(userId, Role.valueOf(role))
    } else {
        null
    }
}



fun HttpSession.setLoginUserDetails(userModel: UserModel) {
    this.setAttribute("userId", userModel.id)
    this.setAttribute("role", userModel.role.name)
    this.maxInactiveInterval = 60 * 60 // 1시간
}