package com.tovelop.maphant.controller

import com.tovelop.maphant.configure.MockupCustomUser
import com.tovelop.maphant.configure.MockupCustomUserToken
import com.tovelop.maphant.type.response.Response
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class SignInDto(val email: String, val password: String)

@RequestMapping("/user")
@RestController
class TestSignInController {
    @PostMapping("/signIn")
    fun signIn(@RequestBody signInDto: SignInDto): ResponseEntity<Response<MockupCustomUser>> {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth != null && auth is MockupCustomUserToken && auth.isAuthenticated) {
            return ResponseEntity.ok(Response.success(auth.details))
        }

        return ResponseEntity.unprocessableEntity().body(Response.error("Invalid credentials"))
    }
}