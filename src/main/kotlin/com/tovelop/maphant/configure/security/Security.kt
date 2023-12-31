package com.tovelop.maphant.configure.security

import com.tovelop.maphant.configure.security.filter.LoginAuthFilter
import com.tovelop.maphant.configure.security.filter.TokenAuthFilter
import com.tovelop.maphant.configure.security.provider.LoginAuthProvider
import com.tovelop.maphant.configure.security.provider.TokenAuthProvider
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
    lateinit var manager: AuthenticationManager

    @Autowired
    lateinit var loginAuthProvider: LoginAuthProvider

    @Autowired
    lateinit var tokenAuthProvider: TokenAuthProvider

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain? {
        http.csrf { it.disable() }
        http.addFilterBefore(
            LoginAuthFilter(authenticationManager(http)),
            UsernamePasswordAuthenticationFilter::class.java
        ).addFilterAfter(
            TokenAuthFilter(authenticationManager(http)),
            LoginAuthFilter::class.java
        ).sessionManagement {
            it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            it.disable()
        }
        http.authorizeHttpRequests { authorize -> authorize
            .requestMatchers("/admin/**").hasRole("admin")
            .anyRequest().permitAll()
        }

        http.authenticationManager(authenticationManager(http))
        return http.build()
    }

    fun authenticationManager(http: HttpSecurity): AuthenticationManager {
        if (this::manager.isInitialized) return this.manager

        val managerBuilder = http.getSharedObject(AuthenticationManagerBuilder::class.java)
        managerBuilder.authenticationProvider(loginAuthProvider)
        managerBuilder.authenticationProvider(tokenAuthProvider)

        this.manager = managerBuilder.build()
        return this.manager
    }
}