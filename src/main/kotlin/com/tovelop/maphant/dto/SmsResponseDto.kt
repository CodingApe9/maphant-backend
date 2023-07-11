package com.tovelop.maphant.dto

import com.fasterxml.jackson.annotation.JsonIgnore

class SmsResponseDto {
    lateinit var requestId: String
    lateinit var requestTime: String
    lateinit var statusCode: String
    lateinit var statusName: String

    @JsonIgnore
    lateinit var smsConfirmNum: String
}


