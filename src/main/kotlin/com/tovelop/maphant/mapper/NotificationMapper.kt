package com.tovelop.maphant.mapper

import com.tovelop.maphant.dto.FcmMessageDTO
import com.tovelop.maphant.dto.NotificationDBDTO
import org.apache.ibatis.annotations.Mapper
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Mapper
@Repository
interface NotificationMapper {
    fun getNotificationsByUserId(userId: Int): List<NotificationDBDTO>
    fun createNotification(notification: FcmMessageDTO, jsonStr: String)
    fun updateNotification(id: Int)
}