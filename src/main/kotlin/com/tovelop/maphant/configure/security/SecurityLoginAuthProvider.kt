package com.tovelop.maphant.configure.security

import com.tovelop.maphant.configure.MockupCustomUserService
import com.tovelop.maphant.configure.MockupCustomUserToken
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class SecurityLoginAuthProvider(
    @Autowired val userDataService: MockupCustomUserService,
    @Autowired val tokenRepo: SecurityTokenRepository
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication?): Authentication {
        if (authentication == null) {
            throw SecurityException("No authentication")
        }

        val usernamePasswordAuthenticationToken = authentication as SecurityLoginAuthToken
        val userData = userDataService.loadUserByUsername(usernamePasswordAuthenticationToken.name)

        if (userData.password != usernamePasswordAuthenticationToken.credentials) {
            throw BadCredentialsException("Invalid Password")
        }

        val auth = tokenRepo.generateToken(userData)
        return SecurityLoginAuthToken(auth.first, auth.second, userData)
    }

    override fun supports(authentication: Class<*>?): Boolean {
        if (authentication == null) return false

        return authentication == SecurityLoginAuthToken::class.java
    }
}