package com.tovelop.maphant.dto

data class ChangeInfoDTO(
    val email: String,
    val nickname: String?,
    val oldPassword: String?,
    val newPassword: String?,
    val newPasswordCheck: String?,
    val phNum: String?
)
