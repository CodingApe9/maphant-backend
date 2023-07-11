package com.tovelop.maphant.configure.security

import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder


@Primary
@Component
class PasswordEncoderBcrypt : PasswordEncoder {

    private val bCryptPasswordEncoder = BCryptPasswordEncoder()

    override fun encode(rawPassword: CharSequence?): String {
        return bCryptPasswordEncoder.encode(rawPassword)
    }

    override fun matches(rawPassword: CharSequence?, encodedPassword: String?): Boolean {
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword)
    }
}
