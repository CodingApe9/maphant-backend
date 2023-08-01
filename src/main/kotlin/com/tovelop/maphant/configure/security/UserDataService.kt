package com.tovelop.maphant.configure.security

import com.tovelop.maphant.dto.MockupUserDTO
import com.tovelop.maphant.dto.UserDTO
import com.tovelop.maphant.mapper.UserDataMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.time.LocalDate

@Configuration
class UserDataService(@Autowired val userDataMapper: UserDataMapper): UserDetailsService {
    override fun loadUserByUsername(username: String): UserData {
        val user = userDataMapper.findUserByEmail(username) ?: throw UsernameNotFoundException("Not found")
        return UserData(username, user.password, user)
    }
}