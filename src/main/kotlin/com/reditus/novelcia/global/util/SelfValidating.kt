package com.reditus.novelcia.global.util

import jakarta.validation.ConstraintViolationException
import jakarta.validation.Validation

abstract class SelfValidating {
    private val validator = Validation.buildDefaultValidatorFactory().validator

    fun validateSelf() {
        val constraintViolations = validator.validate(this)
        if (constraintViolations.isNotEmpty()) {
            throw ConstraintViolationException(constraintViolations)
        }
    }
}