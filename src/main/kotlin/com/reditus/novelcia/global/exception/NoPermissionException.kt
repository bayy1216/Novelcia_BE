package com.reditus.novelcia.global.exception

class NoPermissionException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause) {
}