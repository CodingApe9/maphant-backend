package com.tovelop.maphant.exception

import com.tovelop.maphant.controller.AwsS3Controller
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MaxUploadSizeExceededException

@RestControllerAdvice(assignableTypes = [AwsS3Controller::class])
class UploadLogExceptionHandler {
    @ExceptionHandler(value = [MaxUploadSizeExceededException::class, IllegalArgumentException::class])
    fun handleException(e: RuntimeException): ResponseEntity<HashMap<String, Any>> {
        val result: HashMap<String, Any> = HashMap<String, Any>()
        result.put("status", false)
        result.put("message", e.message as String)
        return ResponseEntity<HashMap<String, Any>>(result, HttpStatus.FORBIDDEN)
    }
}