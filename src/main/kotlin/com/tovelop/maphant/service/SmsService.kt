package com.tovelop.maphant.service

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.tovelop.maphant.dto.SmsMessageReqDto
import com.tovelop.maphant.dto.SmsRequestDto
import com.tovelop.maphant.dto.SmsResponseDto
import com.tovelop.maphant.utils.RandomGenerator
import com.tovelop.maphant.utils.ValidationHelper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.apache.commons.codec.binary.Base64
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import java.io.UnsupportedEncodingException
import java.net.URISyntaxException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Service
class SmsService(@Autowired val redisService: RedisService) {
    @Value("\${naver-cloud-sms.accessKey}")
    private lateinit var accessKey: String

    @Value("\${naver-cloud-sms.secretKey}")
    private lateinit var secretKey: String

    @Value("\${naver-cloud-sms.serviceId}")
    private lateinit var serviceId: String

    @Value("\${naver-cloud-sms.senderPhone}")
    private lateinit var phone: String

    @Throws(
        JsonProcessingException::class,
        RestClientException::class,
        URISyntaxException::class,
        InvalidKeyException::class,
        NoSuchAlgorithmException::class,
        UnsupportedEncodingException::class
    )
    fun sendSms(content: String, sendToList: List<String>): Boolean {
        // 전화번호 유효성 검사
        if (sendToList.any { !ValidationHelper.isValidPhoneNum(it) }) return false
        // 전화번호가 유효하면 - 제거 (ncloude sens에서는 -가 빠져야 함)
        val sendToListEncoded = sendToList.map { SmsMessageReqDto(it.replace("-", "")) }

        // 현재시간
        val time = System.currentTimeMillis().toString()

        // 헤더세팅
        val headers =
            okhttp3.Headers.Builder().add("Content-Type", "application/json").add("x-ncp-apigw-timestamp", time)
                .add("x-ncp-iam-access-key", accessKey).add("x-ncp-apigw-signature-v2", getSignature(time) as String)
                .build()

        // api 요청 양식에 맞춰 세팅
        val request = SmsRequestDto(
            type = "SMS",
            contentType = "COMM",
            countryCode = "82",
            from = phone,
            content = content,
            messages = sendToListEncoded
        )

        // request를 json 형태로 변환
        val objectMapper = ObjectMapper()
        val body: String = objectMapper.writeValueAsString(request)

        // OkHttpClient를 사용하여 외부 API에 요청 보내기
        val client = OkHttpClient()
        val requestObj = Request.Builder().url("https://sens.apigw.ntruss.com/sms/v2/services/$serviceId/messages")
            .post(body.toRequestBody("application/json".toMediaType())).headers(headers).build()

        val response = client.newCall(requestObj).execute()
        val responseBody = response.body?.string()

        val result = objectMapper.readValue(responseBody, SmsResponseDto::class.java)
        return result.statusName == "success"
    }

    @Throws(NoSuchAlgorithmException::class, UnsupportedEncodingException::class, InvalidKeyException::class)
    private fun getSignature(time: String?): String? {
        val space = " "
        val newLine = "\n"
        val method = "POST"
        val url = "/sms/v2/services/$serviceId/messages"
        val accessKey = accessKey
        val secretKey = secretKey
        val message =
            StringBuilder().append(method).append(space).append(url).append(newLine).append(time).append(newLine)
                .append(accessKey).toString()
        val signingKey = SecretKeySpec(secretKey.toByteArray(charset("UTF-8")), "HmacSHA256")
        val mac: Mac = Mac.getInstance("HmacSHA256")
        mac.init(signingKey)
        val rawHmac: ByteArray = mac.doFinal(message.toByteArray(charset("UTF-8")))
        return Base64.encodeBase64String(rawHmac)
    }

    fun sendVerification(phoneNum: String): Boolean {
        var key: String
        while (true) {
            key = RandomGenerator.generateRandomNumber(6)
            println("key: $key")
            if (redisService.setnx("SMS_CODE|$key", phoneNum)) break
        }

        return sendSms("[과끼리 인증]\n인증 코드는 [$key] 입니다.", listOf(phoneNum))
    }

    fun checkVerification(phoneNum: String, key: String): Boolean {
        val redisKey = "SMS_CODE|$key"
        val redisValue = redisService.get(redisKey)
        if (redisValue == null || redisValue != phoneNum) return false

        return true
    }
}