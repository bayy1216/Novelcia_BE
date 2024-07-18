package com.reditus.novelcia.global.exception

class ElementConflictException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)