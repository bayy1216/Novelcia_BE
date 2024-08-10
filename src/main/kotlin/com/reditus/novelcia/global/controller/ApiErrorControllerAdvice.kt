package com.reditus.novelcia.global.controller

import com.reditus.novelcia.global.exception.ElementConflictException
import com.reditus.novelcia.global.exception.NoPermissionException
import com.reditus.novelcia.global.exception.NotAuthorizationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiErrorControllerAdvice {
    private val log = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun illegalArgumentException(e: IllegalArgumentException): ApiResponse<Unit> {
        log.info("IllegalArgumentException", e)
        return ApiResponse.fail(
            message = e.message ?: "COMMON-ILLEGAL-ARGUMENT-EXCEPTION",
            errorCode = "COMMON-ILLEGAL-ARGUMENT",
        )
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun illegalStateException(e: IllegalStateException): ApiResponse<Unit> {
        log.error("IllegalStateException", e)
        return ApiResponse.fail(
            message = e.message ?: "COMMON-ILLEGAL-STATE-EXCEPTION",
            errorCode = "COMMON-ILLEGAL-STATE",
        )
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun jwtException(e: NotAuthorizationException): ApiResponse<Unit> {
        return ApiResponse.fail(
            message = e.message ?: "COMMON-UNAUTHORIZED-EXCEPTION",
            errorCode = "UNAUTHORIZED",
        )
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun resourceNotFoundException(e: NoSuchElementException): ApiResponse<Unit> {
        log.error("NoSuchElementException", e)
        return ApiResponse.fail(
            message = "해당 리소스를 찾을 수 없습니다.",
            errorCode = "NOT-FOUND",
        )
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun noPermissionException(e: NoPermissionException): ApiResponse<Unit> {
        log.error("NoPermissionException", e)
        return ApiResponse.fail(
            message = e.message ?: "COMMON-NO-PERMISSION-EXCEPTION",
            errorCode = "NO-PERMISSION",
        )
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    fun resourceConflictException(e: ElementConflictException): ApiResponse<Unit> {
        log.error("IllegalStateException", e)
        return ApiResponse.fail(
            message = e.message ?: "COMMON-CONFLICT-EXCEPTION",
            errorCode = "CONFLICT",
        )
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun runtimeException(e: RuntimeException): ApiResponse<Unit> {
        log.error("RuntimeException", e)
        return ApiResponse.fail(
            message = "서버에서 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
            errorCode = "COMMON-RUNTIME-EXCEPTION",
        )
    }
}