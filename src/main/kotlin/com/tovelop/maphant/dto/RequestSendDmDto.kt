package com.tovelop.maphant.dto

import jakarta.validation.constraints.NotNull


data class RequestSendDmDto(
    @field:NotNull(message = "receiver_id는 필수 입력값입니다.")
    val receiver_id: Int?,
    @field:NotNull(message = "content는 필수 입력값입니다.")
    val content: String?
)
