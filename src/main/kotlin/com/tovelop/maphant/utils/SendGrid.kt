package com.tovelop.maphant.utils

import com.sendgrid.*
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Content
import com.sendgrid.helpers.mail.objects.Email
import com.tovelop.maphant.service.RedisService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class SendGrid(@Autowired val redisService: RedisService) {
    @Value("\${SEND_GRID_API}")
    val apiKey: String = ""
    val from = Email("admin@ssda.dawoony.com", "과끼리 관리자")

    fun sendSignUp(email: String) {
        val token = saveEmailToken(email)
        val to = Email(email)
        val subject = "과끼리 회원가입 인증 코드 발송"
        val content = Content("text/html", "code")
        val mail = Mail(from, subject, to, content)
        mail.setTemplateId("d-cc500a28387545d7a285cc1fd9c70481")
        mail.personalization[0].addDynamicTemplateData("token", token)
        mail.personalization[0].addDynamicTemplateData("email", email)
        return send(mail)
    }

    fun sendChangePW(email: String) {
        val token = saveEmailToken(email)
        val to = Email(email)
        val subject = "과끼리 비밀번호 변경 인증 코드 발송"
        val content = Content("text/html", "code")
        val mail = Mail(from, subject, to, content)
        mail.setTemplateId("d-423a177b854d4faca15ab1cec6a137c5")
        mail.personalization[0].addDynamicTemplateData("token", token)
        return send(mail)
    }

    fun send(mail: Mail) {
        val sg = SendGrid(apiKey)
        val request = Request()
        try {
            request.method = Method.POST
            request.endpoint = "mail/send"
            request.body = mail.build()

            val response: Response = sg.api(request)
        } catch (e: Exception) {
            throw e
        }
    }

    fun saveEmailToken(email: String): String {
        val random = RandomGenerator.generateRandomNumber(6)
        val isEmailExist = redisService.get(email) == null

        if (isEmailExist)
            redisService.del(email)

        redisService.set(email, random)
        return random
    }

    fun confirmEmailToken(email: String, token: String): Boolean {
        val orgToken = redisService.get(email)
        return orgToken == token
    }
}

