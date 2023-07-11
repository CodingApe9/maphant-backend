package com.tovelop.maphant.configure.security

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.security.MessageDigest

@Component
class PasswordEncoderSHA256 : PasswordEncoder {

    override fun encode(rawPassword: CharSequence): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(rawPassword.toString().toByteArray())
        return hash.joinToString(separator = "") { "%02x".format(it) }
    }

    override fun matches(rawPassword: CharSequence, encodedPassword: String): Boolean {
        return encode(rawPassword) == encodedPassword
    }
}