package com.tovelop.maphant.dto


data class SmsRequestDto(
    val type: String,
    val contentType: String,
    val countryCode: String,
    val from: String,
    val content: String,
    val messages: List<MessageDto>,
)
