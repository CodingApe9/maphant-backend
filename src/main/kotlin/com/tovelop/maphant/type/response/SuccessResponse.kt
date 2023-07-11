package com.tovelop.maphant.type.response

class SuccessResponse<T>(val data: T) : Response<T> {
    override val success: Boolean = true
}