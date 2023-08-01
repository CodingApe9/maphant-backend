package com.tovelop.maphant.dto

data class NewPasswordDTO(
    val email: String,
    val password: String,
    val passwordChk: String
)
