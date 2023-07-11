package com.tovelop.maphant.configure.security

import com.tovelop.maphant.configure.MockupCustomUserService
import com.tovelop.maphant.configure.MockupCustomUserToken
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter

class MockupFilter(val mockupCustomUserService: MockupCustomUserService, manager: AuthenticationManager) :
    AbstractAuthenticationProcessingFilter("/**", manager) {
    override fun attemptAuthentication(request: HttpServletRequest?, response: HttpServletResponse?): Authentication {
        val email = "mockup@test.com"

        val mockup = mockupCustomUserService.loadUserByUsername(email)
        mockup.zeroisePassword()

        return MockupCustomUserToken(email, "mockup", mockup)
    }


    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest

        if (httpRequest.getHeader("x-mockup-auth") != null) {
            super.doFilter(request, response, chain)
        } else {
            chain.doFilter(request, response)
        }
    }

    override fun successfulAuthentication(
        request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain, authResult: Authentication
    ) {
        SecurityContextHolder.getContext().authentication = authResult
        chain.doFilter(request, response)
    }
}