package com.tovelop.maphant.dto

import java.time.LocalDateTime

data class UploadLogDTO(
    val user_id: Int,
    val file_size: Int,
    val url: String,
    val upload_date: LocalDateTime,
)


