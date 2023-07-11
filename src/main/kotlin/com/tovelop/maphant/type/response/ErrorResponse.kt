package com.tovelop.maphant.type.response

class ErrorResponse<T>(val errors: Any) : Response<T> {
    override val success: Boolean = false
}