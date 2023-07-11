package com.tovelop.maphant.configure.security

import com.tovelop.maphant.configure.MockupCustomUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
class Security {
    @Autowired
    lateinit var mockupCustomUserService: MockupCustomUserService
    lateinit var manager: AuthenticationManager

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain? {
        http.csrf { it.disable() }.authorizeHttpRequests { authorize ->
            authorize.anyRequest().permitAll()
        }.addFilterAfter(
            MockupFilter(mockupCustomUserService, authenticationManager(http)),
            UsernamePasswordAuthenticationFilter::class.java
        ).sessionManagement {
            it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            it.disable()
        }.addFilterBefore(
            SecurityLoginAuthFilter(authenticationManager(http)),
            UsernamePasswordAuthenticationFilter::class.java
        )

        http.authenticationManager(authenticationManager(http))
        return http.build()
    }

    @Bean
    fun authenticationManager(http: HttpSecurity): AuthenticationManager {
        if (this::manager.isInitialized) return this.manager

        val managerBuilder = http.getSharedObject(AuthenticationManagerBuilder::class.java)!!
        managerBuilder.userDetailsService(mockupCustomUserService)

        this.manager = managerBuilder.build()
        return this.manager
    }
}