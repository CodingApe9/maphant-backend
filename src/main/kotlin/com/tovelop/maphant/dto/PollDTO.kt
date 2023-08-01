package com.tovelop.maphant.dto

import java.time.LocalDateTime

data class PollDTO(
    val id: Int? = null,
    val boardId: Int,
    val title: String,
    val options: List<String>,
    val expireDateTime: LocalDateTime?,
    var state: Char = 't'
)
