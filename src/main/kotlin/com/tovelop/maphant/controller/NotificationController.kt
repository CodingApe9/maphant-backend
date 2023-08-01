package com.tovelop.maphant.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.tovelop.maphant.dto.NotificationResponseDTO
import com.tovelop.maphant.service.NotificationService
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/notifications")
class NotificationController(private val notificationService: NotificationService) {

    @GetMapping("/{userId}")
    fun getNotificationsByUserId(@PathVariable userId: Int): List<NotificationResponseDTO> {
        return notificationService.getNotificationsByUserId(userId).map {
            NotificationResponseDTO(
                id = it.id,
                etc = ObjectMapper().readValue(it.etc, Map::class.java),
                title = it.title,
                body = it.body,
                createdAt = it.createdAt,
                readAt = it.readAt
            )
        }
    }

    @GetMapping("{userId}/{notificationId}")
    fun updateNotification(
        @PathVariable userId: Int,
        @PathVariable notificationId: Int
    ) {
        notificationService.updateNotification(notificationId)
    }
}
