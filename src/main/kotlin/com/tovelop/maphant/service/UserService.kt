package com.tovelop.maphant.service

import com.tovelop.maphant.dto.UserDTO
import com.tovelop.maphant.mapper.UserMapper
import com.tovelop.maphant.utils.ValidationHelper
import org.springframework.stereotype.Service


@Service
class UserService(val mapper: UserMapper) {
    fun insertCategoryMajorByEmail(email: String, category: String, major: String) {
        mapper.insertCategoryIdMajorIdByUserId(
            mapper.findUserIdByUserEmail(email),
            mapper.findCategoryIdByCategoryName(category),
            mapper.findMajorIdByMajorName(major)
        )
    }
    fun getAllCategories() = mapper.getAllCategories()
    fun getAllMajors() = mapper.getAllMajors()
    fun getAllUnivNames() = mapper.getAllUnivNames()
    fun isValidatedEmail(userId: Int) = mapper.findStateByUserId(userId) == 1
    fun findPasswordByEmail(email: String) = mapper.findPasswordByEmail(email)
    fun findNicknameByEmail(email: String) = mapper.findNicknameByEmail(email)
    fun updateUserState(email: String, state: Int) {
        mapper.updateUserState(email, state)
    }

    fun updateUserPasswordByEmail(email: String, newPassword: String) {
        mapper.updateUserPasswordByEmail(email, newPassword)
    }

    fun updateUserNicknameByEmail(email: String, newNickname: String) {
        mapper.updateUserNicknameByEmail(email, newNickname)
    }

    fun updateUserPhoneNumByEmail(email: String, newPhoneNum: String) {
        mapper.updateUserPhoneNumByEmail(email, newPhoneNum)
    }

    fun signUp(user: UserDTO): Boolean {
        insertUser(user)
        return true
    }

    fun login(email: String, password: String): String? {
        // 로그인 로직
        val user = getUser(listOf(email))
        if (user != null && user.password == password) {
            return user.id.toString()
        }
        return null
    }

    fun matchEmail(email: String, univId: Int?): Boolean {
        val universityName = this.extractFromEmail(email)
        val universityUrl = this.extractFromUrl(mapper.findUniversityUrlBy(univId))
        return universityName == universityUrl
    }

    fun getUser(emails: List<String>): UserDTO? {
        // 사용자 조회 로직
        return mapper.findUserByEmail(emails).firstOrNull()
    }

    fun insertUser(user: UserDTO) {
        // 사용자 추가 로직
        mapper.insertUser(user)
    }

    fun findUniversityIdBy(univName: String): Int? {
        return mapper.findUniversityIdBy(univName)
    }

    fun isPasswordValid(password: String): Boolean {
        return ValidationHelper.isValidPassword(password)
    }

    fun isEmailValid(email: String): Boolean {
        return ValidationHelper.isUniversityEmail(email)
    }

    fun findEmailBysNo(sNo: String, phoneNum: String): String? {
        return mapper.findEmailBysNo(sNo, phoneNum)
    }

    fun isNicknameValid(nickname: String): Boolean {
        return ValidationHelper.isAlphaNumericKorean(nickname)
    }

    fun isUniversityValid(univId: Int?): Boolean {
        if (univId == null) {
            return true
        }
        return mapper.isUniversityExist(univId)
    }

    fun isDuplicateEmail(email: String): Boolean {
        return mapper.countSameEmails(email) > 0
    }

    fun isDuplicateNickname(nickname: String): Boolean {
        return mapper.countSameNickName(nickname) > 0
    }

    fun isDuplicatePhoneNum(phoneNum: String): Boolean {
        return mapper.countSamePhoneNum(phoneNum) > 0
    }

    fun extractFromEmail(email: String): String? {
        val pattern = Regex("""(?<=@)[^.]+\.(ac\.kr|edu)""")
        val matchResult = pattern.find(email)
        return matchResult?.value
    }

    fun extractFromUrl(url: String): String? {
        val pattern = """(?<=\/\/|www\.)[^\.]+\.(ac\.kr|edu)""".toRegex()
        val matchResult = pattern.find(url)
        return matchResult?.value
    }

    fun updateUserRole(role: String, id: Int){
        mapper.updateUserRole(role, id)
    }
}
