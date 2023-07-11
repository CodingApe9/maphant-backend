package com.tovelop.maphant.type.response

interface Response<T> {
    val success: Boolean

    companion object {
        fun <T> success(data: T): Response<T> {
            return SuccessResponse(data)
        }

        fun stateOnly(isSuccess: Boolean): ResponseUnit {
            return UnitResponse(isSuccess)
        }

        fun <T> error(errors: Any): Response<T> {
            return ErrorResponse(errors)
        }
    }
}

