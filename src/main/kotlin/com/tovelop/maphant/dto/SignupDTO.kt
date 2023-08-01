package com.tovelop.maphant.dto

import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDate

data class SignupDTO(
    val email: String,
    val password: String,
    val passwordCheck: String,
    val nickname: String,
    val name: String,
    val sno: String,
    val phNum: String?,
    val univName: String
) {
    fun toUserDTO(univId: Int, passwordEncoder: PasswordEncoder): UserDTO {
        return UserDTO(
            email = email,
            password = passwordEncoder.encode(password),
            nickname = nickname,
            name = name,
            phNum = null,
            sno = sno,
//            createdAt = LocalDate.now(),
            role = "user",
            state = 0,
//            agreedAt = LocalDate.now(),
//            lastmodifiedAt = LocalDate.now(),
            univId = univId
        )
    }
}
