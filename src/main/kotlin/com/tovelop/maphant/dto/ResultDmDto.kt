package com.tovelop.maphant.dto

import java.time.LocalDateTime

data class ResultDmDto(
    val id: Int?,
    val is_me: Boolean,
    val content: String,
    val time: LocalDateTime,
    val is_read: Boolean,
    val room_id: Int,
    var visible: VisibleChoices

)