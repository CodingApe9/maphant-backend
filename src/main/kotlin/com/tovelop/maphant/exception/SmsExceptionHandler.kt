package com.tovelop.maphant.exception

import com.tovelop.maphant.type.response.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class SmsExceptionHandler {
    @ExceptionHandler(value = [IllegalArgumentException::class])
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleException(e: IllegalArgumentException): ErrorResponse<String> {
        val errorMessage = e.message ?: "알 수 없는 에러 발생"
        return ErrorResponse(errorMessage)
    }
}