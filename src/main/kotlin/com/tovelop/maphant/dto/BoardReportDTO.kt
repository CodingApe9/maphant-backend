package com.tovelop.maphant.dto

import java.time.LocalDateTime

data class BoardReportDTO(
    val boardId: Int,
    val userId: Int,
    val reportId: Int,
    val reportedAt: LocalDateTime,
    val id: Int?
)
