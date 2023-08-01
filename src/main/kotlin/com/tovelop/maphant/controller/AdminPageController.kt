package com.tovelop.maphant.controller

import com.tovelop.maphant.configure.security.token.TokenAuthToken
import com.tovelop.maphant.dto.BoardReportDTO
import com.tovelop.maphant.service.AdminPageService
import com.tovelop.maphant.service.BoardService
import com.tovelop.maphant.service.UserService
import com.tovelop.maphant.type.response.Response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin")
class AdminPageController(@Autowired val adminPageService: AdminPageService) {
    @GetMapping("/reportlist/board")
    fun listBoardReport() /*: ResponseEntity<Response<List<BoardReportDTO>>>*/ {
        // 검색 후 비어있으면 비어있다고 에러처리
        // 아니면 보드리포트DTO 리스트 반환
    }
}
