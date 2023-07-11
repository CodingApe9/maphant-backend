package com.tovelop.maphant

import com.tovelop.maphant.utils.ValidationHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ValidationHelperTest {
    @Test
    fun testIsAlphaNumeric() {
        Assertions.assertTrue(ValidationHelper.isAlphaNumeric("Test123"))
        Assertions.assertFalse(ValidationHelper.isAlphaNumeric("Test123@"))
        Assertions.assertFalse(ValidationHelper.isAlphaNumeric("Test 123"))
        Assertions.assertFalse(ValidationHelper.isAlphaNumeric("Test_123"))
    }

    @Test
    fun testIsAlphaNumericKorean() {
        Assertions.assertTrue(ValidationHelper.isAlphaNumericKorean("테스트123"))
        Assertions.assertFalse(ValidationHelper.isAlphaNumericKorean("테스트123@"))
        Assertions.assertFalse(ValidationHelper.isAlphaNumericKorean("테스트 123"))
        Assertions.assertFalse(ValidationHelper.isAlphaNumericKorean("테스트_123"))
    }

    @Test
    fun testIsValidEmail() {
        Assertions.assertTrue(ValidationHelper.isValidEmail("test@example.com"))
        Assertions.assertTrue(ValidationHelper.isValidEmail("test.test@example.com"))
        Assertions.assertTrue(ValidationHelper.isValidEmail("test.test.test@example.co.uk"))
        Assertions.assertFalse(ValidationHelper.isValidEmail("test_example.com"))
        Assertions.assertFalse(ValidationHelper.isValidEmail("test@.com"))
        Assertions.assertFalse(ValidationHelper.isValidEmail("test"))
    }

    @Test
    fun testIsValidPassword() {
        Assertions.assertTrue(ValidationHelper.isValidPassword("Test@1234"))
        Assertions.assertFalse(ValidationHelper.isValidPassword("test"))
        Assertions.assertFalse(ValidationHelper.isValidPassword("Test1234"))
        Assertions.assertFalse(ValidationHelper.isValidPassword("test@1234"))
    }

    @Test
    fun testIsValidPhoneNum() {
        Assertions.assertTrue(ValidationHelper.isValidPhoneNum("010-1234-5678"))
        Assertions.assertTrue(ValidationHelper.isValidPhoneNum("010-123-5678"))
        Assertions.assertFalse(ValidationHelper.isValidPhoneNum("12345678"))
        Assertions.assertFalse(ValidationHelper.isValidPhoneNum("010-1234-56789"))
    }

    @Test
    fun testIsValidNickname() {
        Assertions.assertTrue(ValidationHelper.isValidNickname("NickName"))
        Assertions.assertFalse(ValidationHelper.isValidNickname("ThisIsAVeryLongNickname"))
        Assertions.assertFalse(ValidationHelper.isValidNickname("Nick Name"))
        Assertions.assertFalse(ValidationHelper.isValidNickname("Nick@Name"))
    }

    @Test
    fun testIsValidName() {
        Assertions.assertTrue(ValidationHelper.isValidName("홍길동"))
        Assertions.assertFalse(ValidationHelper.isValidName("John"))
        Assertions.assertFalse(ValidationHelper.isValidName("홍 길 동"))
        Assertions.assertFalse(ValidationHelper.isValidName("홍길동홍길동홍길동홍길동"))
    }

    @Test
    fun testIsUniversityEmail() {
        Assertions.assertTrue(ValidationHelper.isUniversityEmail("test@uni.ac.kr"))
        Assertions.assertTrue(ValidationHelper.isUniversityEmail("test@uni.edu"))
        Assertions.assertTrue(ValidationHelper.isUniversityEmail("test.test@uni.ac.kr"))
        Assertions.assertFalse(ValidationHelper.isUniversityEmail("test.test.test@uni.co.kr"))
        Assertions.assertFalse(ValidationHelper.isUniversityEmail("test@uni.com"))
        Assertions.assertFalse(ValidationHelper.isUniversityEmail("test@.ac.kr"))
        Assertions.assertFalse(ValidationHelper.isUniversityEmail("test"))
    }
}