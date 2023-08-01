package com.tovelop.maphant.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/ping")
class UptimePing {
    @GetMapping("/check")
    fun ping(): String {
        return "pong"
    }

    @GetMapping("/jenkins")
    fun jenkins(): String {
        return "pong"
    }
}