package com.tovelop.maphant.controller

import com.tovelop.maphant.dto.FcmMessageDTO
import com.tovelop.maphant.service.FcmService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/fcm")
class FcmMessageController(private val fcmService: FcmService) {

    @PostMapping("/send")
    fun send(@RequestBody fcmMessageDTO: FcmMessageDTO): ResponseEntity<Any> {
        return try {
            fcmService.send(fcmMessageDTO)
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}
