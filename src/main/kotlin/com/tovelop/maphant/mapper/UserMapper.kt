package com.tovelop.maphant.mapper

import com.tovelop.maphant.dto.UserDTO
import org.apache.ibatis.annotations.Mapper
import org.springframework.stereotype.Repository

@Mapper
@Repository
interface UserMapper {
    fun insertUser(user: UserDTO)
    fun countSameEmails(email: String): Int
    fun countSameNickName(nickName: String): Int
    fun countSamePhoneInt(phoneInt: String): Int
    fun readAllColumnVal(emails: List<String>): List<UserDTO>
    fun findEmailBy(sNo: String, phoneInt: String): String?
    fun updateUserByEmail(id: Int)
    fun findUniversityIdBy(universityName: String): Int?
    fun isUniversityExist(universityId: Int): Boolean
    fun findUniversityUrlBy(universityId: Int?): String
}