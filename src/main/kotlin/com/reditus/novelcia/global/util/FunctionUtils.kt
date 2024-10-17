package com.reditus.novelcia.global.util

import com.reditus.novelcia.global.exception.NoPermissionException


fun <T> authenticate(
    condition: Boolean,
    message: String = "권한이 없습니다.",
    apply: () -> T,
): T {
    if (!condition) {
        throw NoPermissionException(message)
    }
    return apply()
}