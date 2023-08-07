package com.tovelop.maphant.configure.security.filter

import com.tovelop.maphant.configure.security.token.TokenAuthToken
import com.tovelop.maphant.utils.ResponseJsonWriter.Companion.writeJSON
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneOffset

class CookieAuthFilter(authenticationManager: AuthenticationManager?)
    : AbstractAuthenticationProcessingFilter("/admin/**", authenticationManager) {

    override fun requiresAuthentication(request: HttpServletRequest, response: HttpServletResponse): Boolean {
        return request.servletPath.startsWith("/admin/")
    }

    init {
        this.authenticationManager = authenticationManager
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
        var auth: String? = null
        var timestamp: String? = null
        var sign: String? = null

        val cookies = request?.cookies
        if(cookies != null) {
            for(cookie in cookies) {
                if("auth".equals(cookie.name)) auth = cookie.value
                if("timestamp".equals(cookie.name)) timestamp = cookie.value
                if("sign".equals(cookie.name)) sign = cookie.value
            }
        }


        if(auth == null) throw BadCredentialsException("No auth cookie")
        if(timestamp == null) throw BadCredentialsException("No timestamp cookie")
        if(sign == null) throw BadCredentialsException("No sign cookie")

        if(auth!= "maphant@pubKey") {
            val epoch = System.currentTimeMillis() / 1000
            if(epoch - timestamp.toInt() > 60) {
                throw BadCredentialsException("Timestamp expired")
            }
        }

        val authReq = TokenAuthToken(auth, timestamp.toInt(), sign)

        return this.authenticationManager.authenticate(authReq)
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        if(httpRequest.cookies.size >= 2) {
            super.doFilter(request, response, chain)
        } else {
            chain.doFilter(request, response)
        }
    }

    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication
    ) {
        SecurityContextHolder.getContext().authentication = authResult
        chain.doFilter(request, response)
    }
}