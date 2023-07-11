package com.tovelop.maphant.dto

data class ValidationSignupDTO(
    val email: String? = null,
    val nickname: String? = null,
    val phonenum: String? = null,
    val password: String? = null,
    val passwordCheck: String? = null
)
