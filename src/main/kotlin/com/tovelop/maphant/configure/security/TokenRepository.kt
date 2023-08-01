package com.tovelop.maphant.configure.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.tovelop.maphant.service.RedisService
import com.tovelop.maphant.utils.RandomGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TokenRepository(@Autowired private val redis: RedisService) {
    fun generateToken(userData: UserData): Pair<String, String> {
        val privKey = RandomGenerator.generateRandomString(64)
        val pubKey = generateUniqueToken(privKey)

        val objMapper = ObjectMapper()
        userData.zeroisePassword()
        val value = objMapper.writeValueAsString(userData.getUserData())

        redis.set("LOGIN_AUTH|${pubKey}", "${redis.get("LOGIN_AUTH|${pubKey}")}|${value}")

        return Pair(pubKey, privKey)
    }

    fun generateUniqueToken(testAndSet: String): String {
        while(true) {
            val key = RandomGenerator.generateRandomString(48)

            if(redis.setnx("LOGIN_AUTH|${key}", testAndSet)) {
                return key
            }
        }
    }
}