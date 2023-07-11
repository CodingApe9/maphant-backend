package com.tovelop.maphant.configure.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.tovelop.maphant.utils.ResponseJsonWriter.Companion.writeJSON
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.stereotype.Component

@Component
class SecurityLoginAuthFilter(authenticationManager: AuthenticationManager):
AbstractAuthenticationProcessingFilter(AntPathRequestMatcher("/user/login", "POST"), authenticationManager){

    init {
        this.authenticationManager = authenticationManager

        this.setAuthenticationSuccessHandler { request, response, authentication ->
            run {
                val authResult = authentication as SecurityLoginAuthToken
                val output = mutableMapOf<String, Any>(
                    "status" to true,
                    "pubKey" to authResult.principal,
                    "privateKey" to authResult.credentials
                )

                response.status = 200
                response.writeJSON(output)
            }
        }

        this.setAuthenticationFailureHandler { request, response, exception ->
            run {
                val output = mutableMapOf<String, Any>(
                    "status" to false,
                    "message" to (exception.message ?: "unexpected error")
                )

                response.status = 401
                response.writeJSON(output)
            }
        }

    }

    override fun attemptAuthentication(request: HttpServletRequest?, response: HttpServletResponse?): Authentication? {
        val user = request?.getParameter("email") ?: throw BadCredentialsException("no email field")
        val password = request.getParameter("password") ?: throw BadCredentialsException("no password field")

        val authReq = SecurityLoginAuthToken(user, password)

        return this.authenticationManager.authenticate(authReq)
    }
}