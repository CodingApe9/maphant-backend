package com.tovelop.maphant.controller

import com.tovelop.maphant.dto.EmailAuthDTO
import com.tovelop.maphant.dto.ValidationSignupDTO
import com.tovelop.maphant.service.UserService
import com.tovelop.maphant.type.response.Response
import com.tovelop.maphant.type.response.ResponseUnit
import com.tovelop.maphant.utils.SendGrid
import com.tovelop.maphant.utils.ValidationHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/email")
class EmailAuthController(@Autowired val sendGrid: SendGrid) {
    @PostMapping("/sendsignup")
    fun sendauthcode(
        @RequestBody eamilAuthDTO: EmailAuthDTO
    ): ResponseEntity<ResponseUnit> {
        if (!ValidationHelper.isUniversityEmail(eamilAuthDTO.email))
            return ResponseEntity.badRequest().body(Response.error("형식에 맞지 않는 이메일입니다."))
        sendGrid.sendSignUp(eamilAuthDTO.email)
        return ResponseEntity.ok(Response.stateOnly(true))
    }

    @PostMapping("/confirmEmail")
    fun confirmEamil(@RequestBody emailAuthDTO: EmailAuthDTO): ResponseEntity<ResponseUnit> {
        if (emailAuthDTO.authcode.isNullOrEmpty()) return ResponseEntity.badRequest()
            .body(Response.error("인증코드를 입력해 주세요."))
        if (!sendGrid.confirmEmailToken(emailAuthDTO.email, emailAuthDTO.authcode))
            return ResponseEntity.badRequest().body(Response.error("인증코드가 일치하지 않습니다."))
        // state -> 1로 바꿔주기
        return ResponseEntity.ok(Response.stateOnly(true))
    }
}