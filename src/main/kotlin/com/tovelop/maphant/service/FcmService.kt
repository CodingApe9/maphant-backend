package com.tovelop.maphant.service

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.tovelop.maphant.dto.FcmMessageDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FcmService(@Autowired private val notificationService: NotificationService) {

    fun send(fcmMessageDTO: FcmMessageDTO) {
        val messageBuilder = Message.builder()
            .setNotification(
                com.google.firebase.messaging.Notification.builder()
                    .setTitle(fcmMessageDTO.title)
                    .setBody(fcmMessageDTO.body)
                    .build()
            )
            .setToken(fcmMessageDTO.to)
        for (key in fcmMessageDTO.etc.keys) {
            messageBuilder.putData(key, fcmMessageDTO.etc[key])
        }

        val message = messageBuilder.build()

        notificationService.createNotification(fcmMessageDTO)
        val response = FirebaseMessaging.getInstance().send(message)
        println("Successfully sent message: $response")
    }
}
