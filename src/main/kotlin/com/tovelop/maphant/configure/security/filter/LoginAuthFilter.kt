package com.tovelop.maphant.configure.security.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.tovelop.maphant.configure.security.token.LoginAuthToken
import com.tovelop.maphant.dto.LoginDTO
import com.tovelop.maphant.utils.ResponseJsonWriter.Companion.writeJSON
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.stereotype.Component

class LoginAuthFilter(authenticationManager: AuthenticationManager?)
    :AbstractAuthenticationProcessingFilter(AntPathRequestMatcher("/user/login", "POST"), authenticationManager) {

    private val objectMapper = ObjectMapper()

    init {
        this.authenticationManager = authenticationManager
        this.setAuthenticationSuccessHandler { request, response, authentication ->
            run {
                val authResult = authentication as LoginAuthToken
                val output = mutableMapOf<String, Any>(
                    "success" to true,
                    "pubKey" to authResult.principal,
                    "privKey" to authResult.credentials,
                )

                response.status = 200
                response.writeJSON(output)
            }
        }
        this.setAuthenticationFailureHandler { request, response, exception ->
            run {
                val output = mutableMapOf<String, Any>(
                    "success" to false,
                    "message" to (exception.message ?: "unexpected error")
                )

                response.status = 401
                response.writeJSON(output)
            }
        }
    }

    override fun attemptAuthentication(request: HttpServletRequest?, response: HttpServletResponse?): Authentication? {
        val body = request?.reader?.readText()
        val loginReq = objectMapper.readValue(body, LoginDTO::class.java)

        val email = loginReq.email ?: throw BadCredentialsException("no email field")
        val password = loginReq.password ?: throw BadCredentialsException("no password field")

        val authReq = LoginAuthToken(email, password)

        return this.authenticationManager.authenticate(authReq)
    }
}