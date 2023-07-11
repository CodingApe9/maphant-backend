package com.tovelop.maphant.dto

import java.time.LocalDate

data class UploadLogDTO(
    val number: Int,
    val user_id: Int,
    val file_size: Long,
    val upload_date: LocalDate,
    val url: String,
)


