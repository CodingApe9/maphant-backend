package com.tovelop.maphant.configure.security

import org.springframework.stereotype.Component
import org.springframework.security.crypto.password.PasswordEncoder
import java.security.MessageDigest

@Component
class PasswordEncoderSHA512 : PasswordEncoder {
    override fun encode(rawPassword: CharSequence): String {
        val digest = MessageDigest.getInstance("SHA-512")
        val hash = digest.digest(rawPassword.toString().toByteArray())

        return hash.joinToString(separator = "") { "%02x".format(it) }
    }

    override fun matches(rawPassword: CharSequence, encodedPassword: String): Boolean {
        return encode(rawPassword) == encodedPassword
    }
}