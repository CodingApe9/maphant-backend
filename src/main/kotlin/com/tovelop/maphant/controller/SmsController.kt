package com.tovelop.maphant.controller

import com.tovelop.maphant.dto.MessageDto
import com.tovelop.maphant.dto.SmsResponseDto
import com.tovelop.maphant.service.SmsService
import com.tovelop.maphant.type.response.SuccessResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpSession
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.SessionAttribute

@RestController
class SmsController(private val smsService: SmsService) {
    @PostMapping("/sms/send")
    fun sendSms(@RequestBody messageDto: MessageDto, request: HttpServletRequest): SuccessResponse<SmsResponseDto> {
        val result = smsService.sendSms(messageDto) as SmsResponseDto

        //세션에 인증코드 저장
        val session = request.getSession(true)
        session.setAttribute("sms_code", result.smsConfirmNum)

        return SuccessResponse(result)
    }

    @GetMapping("/sms/check")
    fun codeCheck(
        @RequestParam("code") sendCode: String,
        @SessionAttribute(name = "sms_code") code: String
    ): SuccessResponse<String> {
        if (!sendCode.equals(code))
            throw IllegalArgumentException("인증코드가 맞지 않습니다.")

        return SuccessResponse("인증에 성공하였습니다.")
    }
}