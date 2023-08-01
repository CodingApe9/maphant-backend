package com.tovelop.maphant.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.tovelop.maphant.dto.FcmMessageDTO
import com.tovelop.maphant.dto.NotificationDBDTO
import com.tovelop.maphant.mapper.NotificationMapper
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class NotificationService(
    private val notificationMapper: NotificationMapper
) {

    fun getNotificationsByUserId(userId: Int): List<NotificationDBDTO> {
        return notificationMapper.getNotificationsByUserId(userId)
    }

    fun createNotification(notification: FcmMessageDTO) {
        val data: Map<String, String> = notification.etc
        // convert map to json string
        val jsonStr = ObjectMapper().writeValueAsString(data)

        notificationMapper.createNotification(notification, jsonStr)
    }

    fun updateNotification(id: Int) {
        notificationMapper.updateNotification(id)
    }
}