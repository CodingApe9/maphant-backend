package com.tovelop.maphant.configure.security

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class PasswordEncoderTest {
    val sha256 = PasswordEncoderSHA256()
    val sha512 = PasswordEncoderSHA512()
    val bcrypt = PasswordEncoderBcrypt()

    @Test
    @DisplayName("SHA256 Hello")
    fun test1() {
        val pw = "Hello"
        val encoded = sha256.encode(pw)
        sha256.matches(pw, encoded)
        println(encoded)
    }

    @Test
    @DisplayName("SHA512 Hello")
    fun test2() {
        val pw = "Hello"
        val encoded = sha512.encode(pw)
        sha512.matches(pw, encoded)
        println(encoded)
    }

    @Test
    @DisplayName("Bcrypt Hello")
    fun test3() {
        val pw = "Hello"
        val encoded = bcrypt.encode(pw)
        bcrypt.matches(pw, encoded)
        println(encoded)
    }
}