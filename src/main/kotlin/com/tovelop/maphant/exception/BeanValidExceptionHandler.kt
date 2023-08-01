package com.tovelop.maphant.exception

import com.tovelop.maphant.controller.ProfileController
import com.tovelop.maphant.type.response.ErrorResponse
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class BeanValidExceptionHandler {

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleInvalidRequestBodyException(e: MethodArgumentNotValidException): ErrorResponse<String> {
        val allErrors = e.bindingResult.allErrors.map { it.defaultMessage }
        return ErrorResponse(allErrors)
    }

    // @RequestParam과 @PathVariable의 파라미터가 유효하지 않으면 ConstraintViolationException 에러가 발생합니다
    @ExceptionHandler
    fun handleConstraintViolationException(e: ConstraintViolationException): ErrorResponse<String> {
        return ErrorResponse(e.message as String)
    }
}