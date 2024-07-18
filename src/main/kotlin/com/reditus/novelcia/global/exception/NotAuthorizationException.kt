package com.reditus.novelcia.global.exception

class NotAuthorizationException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause) {
}