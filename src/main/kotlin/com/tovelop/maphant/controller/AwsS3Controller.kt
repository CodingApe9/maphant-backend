package com.tovelop.maphant.controller

import com.tovelop.maphant.configure.MockupCustomUserToken
import com.tovelop.maphant.dto.UploadLogDTO
import com.tovelop.maphant.service.AwsS3Service
import com.tovelop.maphant.service.UploadLogService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.multipart.MultipartFile

@RestController
class AwsS3Controller(
    private val awsS3Service: AwsS3Service,
    private val uploadLogService: UploadLogService
) {

    @PostMapping("/upload")
    fun uploadFile(@RequestParam files: List<MultipartFile>): List<UploadLogDTO> {

        if (files.size > 5) {
            // 에러 던지기
            throw MaxUploadSizeExceededException(5)
        }
        // 시큐리티 붙이기전 userId 1로 가정
        // 아직 Security Configure가 끝나지 않아서 익명 유저 토큰도 들어옴.
        // HTTP 헤더에 x-mockup-auth가 있어야 MockupCustomUserToken으로 캐스팅 가능
        // 캐스팅이 안되면 익명 유저 토큰으로 캐스팅 (오류 발생함 - AnonymousAuthenticationToken cannot be cast to MockupCustomUserToken)
        val securityContextHolder = SecurityContextHolder.getContext().authentication as MockupCustomUserToken
        val userId: Int = securityContextHolder.principal

        // 서비스에서 여러 파일 aws에 올려서 url 받기
        val imageUrls: List<String> = awsS3Service.uploadFiles(files)
        //db저장 후 dto 반환
        return uploadLogService.storeUrl(userId, imageUrls, files)
    }



}