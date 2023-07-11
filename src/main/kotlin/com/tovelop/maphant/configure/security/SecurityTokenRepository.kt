package com.tovelop.maphant.configure.security

import com.tovelop.maphant.configure.MockupCustomUser
import com.tovelop.maphant.storage.RedisMockup
import com.tovelop.maphant.utils.RandomGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SecurityTokenRepository(@Autowired private val redis: RedisMockup) {
    fun generateToken(userData: MockupCustomUser): Pair<String, String> {
        val privKey = RandomGenerator.generateRandomString(64)
        val publicKey = generateUniqueToken(privKey)

        return Pair(publicKey, privKey)
    }

    private fun generateUniqueToken(testAndSet: String): String {
        while (true) {
            val key = RandomGenerator.generateRandomString(48)

            if (redis.setnx(key, testAndSet) == 1) {
                return key
            }
        }
    }
}