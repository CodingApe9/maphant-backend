package com.tovelop.maphant.configure.security.provider

import com.fasterxml.jackson.databind.ObjectMapper
import com.tovelop.maphant.configure.security.UserData
import com.tovelop.maphant.configure.security.token.TokenAuthToken
import com.tovelop.maphant.dto.UserDataDTO
import com.tovelop.maphant.service.RedisService
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class TokenAuthProvider(
    private val redis: RedisService
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication {
        if(authentication.isAuthenticated) return authentication

        val token = authentication as TokenAuthToken

        if(token.isExpired()) {
            throw BadCredentialsException("Token expired")
        }

        val principal = token.principal
        val credentials = token.credentials

        val userToken = redis.get("LOGIN_AUTH|${principal}") ?: throw BadCredentialsException("No user")

        if(credentials.third != token.createToken(credentials.second, userToken.substringBefore('|'))) {
            throw BadCredentialsException("Invalid token")
        }

        val objMapper = ObjectMapper()
        val user = objMapper.readValue(userToken.substringAfter('|'), UserDataDTO::class.java)
        val userData = UserData(user.email, user.password, user)

        return TokenAuthToken(principal, credentials.second, credentials.third, userData)
    }

    override fun supports(authentication: Class<*>?): Boolean {
        if(authentication == null) return false

        return authentication == TokenAuthToken::class.java
    }
}