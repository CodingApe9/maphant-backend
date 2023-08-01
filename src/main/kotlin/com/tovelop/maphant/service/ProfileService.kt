package com.tovelop.maphant.service

import com.tovelop.maphant.dto.BoardDTO
import com.tovelop.maphant.dto.BoardResDto
import com.tovelop.maphant.dto.ProfileImageDto
import com.tovelop.maphant.dto.UploadLogDTO
import com.tovelop.maphant.mapper.ProfileMapper
import com.tovelop.maphant.type.paging.Pagination
import com.tovelop.maphant.type.paging.PagingDto
import com.tovelop.maphant.type.paging.PagingResponse
import com.tovelop.maphant.type.response.SuccessResponse
import com.tovelop.maphant.utils.UploadUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import java.util.Collections

@Service
class ProfileService (
    private val uploadUtils: UploadUtils,
    private var profileMapper: ProfileMapper,
    private val uploadLogService: UploadLogService,
){
    //프로필 이미지의 Dto 불러오기
    fun getProfileImage(userId:Int): ProfileImageDto {
        return profileMapper.findImageById(userId)
    }

    //유저가 작성한 글 목록 불러오기
    fun getBoardsList(userId: Int, params: PagingDto):PagingResponse<BoardResDto>{
        //getBoardCount xml 구현
        val count = profileMapper.getBoardCount(userId);

        if(count < 1) {
            return PagingResponse(Collections.emptyList(),null);
        }

        //findAllBoardByIdwithPaging xml 구현
        val pagination = Pagination(count,params);
        val boards = profileMapper.findAllBoardByIdWithPaging(userId,params);

        return PagingResponse(boards,pagination);
    }

    @Transactional
    fun updateProfileImage(userId: Int, imageUrl: String, file: MultipartFile): String {
        if (uploadUtils.isNotImageFile(file.originalFilename as String))
            throw IllegalArgumentException("png, jpeg, jpg에 해당하는 파일만 업로드할 수 있습니다.");

        //uploadLog 남기기
        uploadLogService.storeUrl(userId, imageUrl, file)

        //현재 userId에 해당하는 profile이 있는지 확인
        val profile = profileMapper.findById(userId);

        //profile이 현재 없다면 insert 아니면 update
        if(profile == null) profileMapper.insertProfile(userId, imageUrl)
        else profileMapper.updateProfileImage(userId, imageUrl)

        return imageUrl;
    }
}