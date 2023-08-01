package com.tovelop.maphant.mapper

import org.apache.ibatis.annotations.Mapper
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
@Mapper
interface UploadLogMapper {
    fun insertUploadLog(user_id:Int, file_size:Int, upload_date:LocalDateTime, url:String):Boolean
}