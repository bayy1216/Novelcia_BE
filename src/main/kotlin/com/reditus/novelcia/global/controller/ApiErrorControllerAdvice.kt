package com.reditus.novelcia.global.controller

import com.reditus.novelcia.global.exception.ElementConflictException
import com.reditus.novelcia.global.exception.NoPermissionException
import com.reditus.novelcia.global.exception.NotAuthorizationException
import com.reditus.novelcia.global.security.LoginUserDetails
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingPathVariableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ApiErrorControllerAdvice {
    private val log = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun methodArgumentNotValidException(e: MethodArgumentNotValidException): ApiResponse<Unit> {
        log.info("MethodArgumentNotValidException", e)
        return ApiResponse.fail(
            message = e.bindingResult.fieldError?.defaultMessage ?: "COMMON-VALIDATION-EXCEPTION",
            errorCode = "COMMON-VALIDATION",
        )
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpMessageNotReadable(e: HttpMessageNotReadableException): ApiResponse<Unit> {
        log.info("HttpMessageNotReadableException", e)
        return ApiResponse.fail(
            message = "COMMON-INVALID-REQUEST-EXCEPTION",
            errorCode = "COMMON-INVALID-REQUEST",
        )
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    fun handleHttpRequestMethodNotSupported(e: HttpRequestMethodNotSupportedException): ApiResponse<Unit> {
        log.info("HttpRequestMethodNotSupportedException", e)
        return ApiResponse.fail(
            message = "COMMON-METHOD-NOT-SUPPORTED-EXCEPTION",
            errorCode = "COMMON-METHOD-NOT-SUPPORTED",
        )
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMissingServletRequestPart(e: MissingServletRequestPartException): ApiResponse<Unit> {
        log.info("MissingServletRequestPartException", e)
        return ApiResponse.fail(
            message = "COMMON-MISSING-PART-EXCEPTION",
            errorCode = "COMMON-MISSING-PART",
        )
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMissingServletRequestParameter(e: MissingServletRequestParameterException): ApiResponse<Unit> {
        log.info("MissingServletRequestParameterException", e)
        return ApiResponse.fail(
            message = "COMMON-MISSING-PARAMETER-EXCEPTION",
            errorCode = "COMMON-MISSING-PARAMETER",
        )
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMissingPathVariable(e: MissingPathVariableException): ApiResponse<Unit> {
        log.info("MissingPathVariableException", e)
        return ApiResponse.fail(
            message = "COMMON-MISSING-PATH-VARIABLE-EXCEPTION",
            errorCode = "COMMON-MISSING-PATH-VARIABLE",
        )
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpMediaTypeNotSupported(e: HttpMediaTypeNotSupportedException): ApiResponse<Unit> {
        log.info("HttpMediaTypeNotSupportedException", e)
        return ApiResponse.fail(
            message = "COMMON-MEDIA-TYPE-NOT-SUPPORTED-EXCEPTION",
            errorCode = "COMMON-MEDIA-TYPE-NOT-SUPPORTED",
        )
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHandlerMethodValidationException(e: HandlerMethodValidationException): ApiResponse<Unit> {
        log.info("HandlerMethodValidationException", e)
        return ApiResponse.fail(
            message = e.message,
            errorCode = "COMMON-VALIDATION",
        )
    }

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
    fun runtimeException(
        e: RuntimeException,
        @AuthenticationPrincipal
        loginUserDetails: LoginUserDetails?,
    ): ApiResponse<Unit> {
        val eventId = MDC.get(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY)
        val loginUserId = loginUserDetails?.loginUserId?.value
        log.error("RuntimeException, eventId = {}, loginUserId = {}", eventId, loginUserId, e)
        return ApiResponse.fail(
            message = "서버에서 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
            errorCode = "event:$eventId",
        )
    }
}