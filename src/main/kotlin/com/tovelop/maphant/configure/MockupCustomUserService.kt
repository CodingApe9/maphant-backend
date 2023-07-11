package com.tovelop.maphant.configure

import com.tovelop.maphant.dto.UserDTO
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.time.LocalDate

@Configuration
class MockupCustomUserService : UserDetailsService {
    override fun loadUserByUsername(username: String?): MockupCustomUser {
        if (username == "mockup@test.com") return MockupCustomUser(
            "mockup@test.com", "mockup", UserDTO(
                id = 1,
                email = "test@ks.ac.kr",
                password = "Password123!",
                nickname = "NickName",
                name = "User Name",
                phoneInt = "1234567890",
                sNo = "2017648070",
                create_at = LocalDate.now(),
                role = "user",
                state = "1",
                is_agree = "Yes",
                last_modified_date = LocalDate.now(),
                university_id = 123
            )
        )
        else throw UsernameNotFoundException("User not found")
    }
}