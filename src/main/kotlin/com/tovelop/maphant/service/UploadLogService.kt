package com.tovelop.maphant.service

import com.tovelop.maphant.dto.UploadLogDTO
import com.tovelop.maphant.utils.UploadUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import java.util.ArrayList

@Service
class UploadLogService(private val uploadUtils: UploadUtils) {
    @Transactional
    fun storeUrl(userId:Int, imageUrls: List<String>, files: List<MultipartFile>): ArrayList<UploadLogDTO> {
        val dtoList = ArrayList<UploadLogDTO>()

        for ((index, file) in files.withIndex()) {

            if(uploadUtils.isNotImageFile(file.originalFilename as String))
                throw IllegalArgumentException("png, jpeg, jpg에 해당하는 파일만 업로드할 수 있습니다.");

            val storeUrlDto = UploadLogDTO(
                number = 0, //Auto Increment 사용시에 없어도 될듯
                user_id = userId,
                file_size = file.size,
                upload_date = LocalDate.now(),
                url = imageUrls[index]
            )
            dtoList.add(storeUrlDto)
            /* TODO Mapper 이용해 db 저장 하기 */
        }

        return dtoList
    }
}