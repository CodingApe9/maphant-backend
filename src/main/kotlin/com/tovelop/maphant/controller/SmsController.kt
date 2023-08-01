package com.tovelop.maphant.controller

import com.tovelop.maphant.dto.SmsMessageReqDto
import com.tovelop.maphant.dto.smsMessageConfirmReqDto
import com.tovelop.maphant.service.RedisService
import com.tovelop.maphant.service.SmsService
import com.tovelop.maphant.type.response.Response
import com.tovelop.maphant.type.response.ResponseUnit
import com.tovelop.maphant.utils.ValidationHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SmsController(@Autowired private val smsService: SmsService, @Autowired private val redisService: RedisService) {
    @PostMapping("/sms/send")
    fun sendSms(
        @RequestBody smsMessageReqDto: SmsMessageReqDto,
    ): ResponseEntity<ResponseUnit> {
        if (ValidationHelper.isValidPhoneNum(smsMessageReqDto.to).not()) return ResponseEntity.badRequest()
            .body(Response.error("올바른 전화번호가 아닙니다."))

        return ResponseEntity.ok().body(Response.stateOnly(smsService.sendVerification(smsMessageReqDto.to)))
    }

    @GetMapping("/sms/check")
    fun codeCheck(
        @RequestBody smsMessageConfirmReqDto: smsMessageConfirmReqDto
    ): ResponseEntity<ResponseUnit> {
        val result = smsService.checkVerification(smsMessageConfirmReqDto.to, smsMessageConfirmReqDto.code)

        if (!result) return ResponseEntity.badRequest().body(Response.error("인증번호가 일치하지 않습니다."))

        return ResponseEntity.ok().body(Response.stateOnly(result))
    }
}