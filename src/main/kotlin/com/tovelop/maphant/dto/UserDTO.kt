package com.tovelop.maphant.dto

import java.time.LocalDate

data class UserDTO(
    val id: Int? = null,
    val email: String,
    var password: String,
    val nickname: String,
    val name: String,
    val phoneInt: String,
    val sNo: String,
    val create_at: LocalDate,
    val role: String,
    val state: String,
    val is_agree: String,
    val last_modified_date: LocalDate,
    val university_id: Int?
)
