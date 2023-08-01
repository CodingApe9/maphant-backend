package com.tovelop.maphant.controller

import com.tovelop.maphant.configure.security.token.TokenAuthToken
import com.tovelop.maphant.service.BookmarkService
import com.tovelop.maphant.type.response.Response
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/bookmark")
class BookmarkController(val bookmarkService: BookmarkService) {

    //북마크 추가 api
    @PostMapping("/{boardId}")
    fun bookmark(@PathVariable("boardId") boardId: Int): ResponseEntity<Any> {
        val auth = SecurityContextHolder.getContext().authentication!! as TokenAuthToken

        val bookmarkResult = bookmarkService.insert(auth.getUserData().id!!, boardId)
        if (!bookmarkResult) {
            return ResponseEntity.badRequest().body(
                Response.error<Any>
                    ("이미 등록된 북마크거나 게시물이 삭제되었습니다.")
            )
        }
        return ResponseEntity.ok().body(Response.stateOnly(true))
    }

    //나의 북마크 보기
    @GetMapping("/my-list")
    fun showBookmarks(): ResponseEntity<Any> {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth == null || auth !is TokenAuthToken || !auth.isAuthenticated) {
            return ResponseEntity.badRequest().body("로그인 안됨")
        }

        val bookmarkList = bookmarkService.showBookmarks(auth.getUserData().id!!)
        if (bookmarkList.isFailure) {
            return ResponseEntity.badRequest().body(Response.error<Any>("요청에 실패했습니다."))
        }

        return ResponseEntity.ok().body(Response.success(bookmarkList.getOrNull()))
    }
}