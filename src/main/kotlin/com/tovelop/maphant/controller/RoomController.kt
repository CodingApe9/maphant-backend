package com.tovelop.maphant.controller

import com.tovelop.maphant.configure.security.token.TokenAuthToken
import com.tovelop.maphant.dto.ResultDmDto
import com.tovelop.maphant.type.paging.PagingDto
import com.tovelop.maphant.dto.RoomListResultDto
import com.tovelop.maphant.service.DmService
import com.tovelop.maphant.type.paging.PagingResponse
import com.tovelop.maphant.type.response.SuccessResponse
import jakarta.validation.Valid
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/room")
class RoomController(private val dmService: DmService) {

    @GetMapping
    fun getRoomList(): SuccessResponse<List<RoomListResultDto>> {

        val auth = SecurityContextHolder.getContext().authentication!! as TokenAuthToken
        val userId: Int = auth.getUserData().id!!

        return SuccessResponse(dmService.findRoomList(userId))
    }

    @GetMapping("/{roomId}")
    fun getDmList(
        @ModelAttribute @Valid params: PagingDto,
        @PathVariable("roomId") roomId: Int
    ): SuccessResponse<PagingResponse<ResultDmDto>> {
        val auth = SecurityContextHolder.getContext().authentication!! as TokenAuthToken
        val userId: Int = auth.getUserData().id!!

        // 서비스 호출
        return SuccessResponse(dmService.getDmList(userId, roomId, params))
    }

    @DeleteMapping("/{roomId}")
    fun deleteRoom(@PathVariable("roomId") roomId: Int): SuccessResponse<String> {
        val auth = SecurityContextHolder.getContext().authentication!! as TokenAuthToken
        val userId: Int = auth.getUserData().id!!

        dmService.deleteRoom(userId, roomId);

        return SuccessResponse("해당 대화방을 삭제하였습니다.")
    }
}
