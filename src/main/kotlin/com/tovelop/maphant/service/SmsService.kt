package com.tovelop.maphant.service

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.tovelop.maphant.dto.MessageDto
import com.tovelop.maphant.dto.SmsRequestDto
import com.tovelop.maphant.dto.SmsResponseDto
import org.apache.commons.codec.binary.Base64
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import java.io.UnsupportedEncodingException
import java.net.URI
import java.net.URISyntaxException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.Random
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

@Service
class SmsService {
    @Value("\${naver-cloud-sms.accessKey}")
    private lateinit var accessKey: String;

    @Value("\${naver-cloud-sms.secretKey}")
    private lateinit var secretKey: String;

    @Value("\${naver-cloud-sms.serviceId}")
    private lateinit var serviceId: String;

    @Value("\${naver-cloud-sms.senderPhone}")
    private lateinit var phone: String;

    @Throws(
        JsonProcessingException::class,
        RestClientException::class,
        URISyntaxException::class,
        InvalidKeyException::class,
        NoSuchAlgorithmException::class,
        UnsupportedEncodingException::class
    )
    fun sendSms(messageDto: MessageDto): SmsResponseDto? {

        // 현재시간
        val time = System.currentTimeMillis().toString()

        // 헤더세팅
        val headers = okhttp3.Headers.Builder()
            .add("Content-Type", "application/json")
            .add("x-ncp-apigw-timestamp", time)
            .add("x-ncp-iam-access-key", accessKey)
            .add("x-ncp-apigw-signature-v2", getSignature(time) as String)
            .build()

        val messages: MutableList<MessageDto> = ArrayList()
        messages.add(messageDto)

        val smsConfirmNum: String = createSmsKey()
        // api 요청 양식에 맞춰 세팅
        val request: SmsRequestDto = SmsRequestDto(
            type = "SMS",
            contentType = "COMM",
            countryCode = "82",
            from = phone,
            content = "[sms test] 인증번호 [$smsConfirmNum]를 입력해주세요",
            messages = messages
        )

        // request를 json 형태로 변환
        val objectMapper = ObjectMapper()
        val body: String = objectMapper.writeValueAsString(request)

        // OkHttpClient를 사용하여 외부 API에 요청 보내기
        val client = OkHttpClient()
        val requestObj = Request.Builder()
            .url("https://sens.apigw.ntruss.com/sms/v2/services/$serviceId/messages")
            .post(body.toRequestBody("application/json".toMediaType()))
            .headers(headers)
            .build()

        val response = client.newCall(requestObj).execute()
        val responseBody = response.body?.string()

        // responseBody를 SmsResponseDto 객체로 변환
        val smsResponseDto: SmsResponseDto? = objectMapper.readValue(responseBody, SmsResponseDto::class.java)
        
        //인증코드 set
        if (smsResponseDto != null) {
            smsResponseDto.smsConfirmNum = smsConfirmNum
        }

        return smsResponseDto

    }

    @Throws(NoSuchAlgorithmException::class, UnsupportedEncodingException::class, InvalidKeyException::class)
    fun getSignature(time: String?): String? {
        val space = " "
        val newLine = "\n"
        val method = "POST"
        val url = "/sms/v2/services/" + serviceId + "/messages"
        val accessKey = accessKey
        val secretKey = secretKey
        val message = StringBuilder()
            .append(method)
            .append(space)
            .append(url)
            .append(newLine)
            .append(time)
            .append(newLine)
            .append(accessKey)
            .toString()
        val signingKey = SecretKeySpec(secretKey!!.toByteArray(charset("UTF-8")), "HmacSHA256")
        val mac: Mac = Mac.getInstance("HmacSHA256")
        mac.init(signingKey)
        val rawHmac: ByteArray = mac.doFinal(message.toByteArray(charset("UTF-8")))
        return Base64.encodeBase64String(rawHmac)
    }

    fun createSmsKey(): String {
        val key = StringBuffer()
        val rnd = Random()
        for (i in 0..4) { // 인증코드 5자리
            key.append(rnd.nextInt(10))
        }
        return key.toString()
    }
}