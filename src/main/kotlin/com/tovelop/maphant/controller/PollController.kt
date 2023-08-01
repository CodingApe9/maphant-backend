package com.tovelop.maphant.controller

import com.tovelop.maphant.configure.security.token.TokenAuthToken
import com.tovelop.maphant.dto.PollDTO
import com.tovelop.maphant.service.PollService
import com.tovelop.maphant.type.response.Response
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/poll")
class PollController(val pollService: PollService) {

    @GetMapping("/{board_id}")
    @ResponseBody
    fun getPoll(@PathVariable("board_id") boardId: Int) =
        mutableMapOf("poll_id" to pollService.getPollIdByBoardId(boardId))

    @PostMapping("/") // 투표 생성
    fun createPoll(@RequestBody poll: PollDTO): ResponseEntity<Any> {
        pollService.createPoll(poll)
        return ResponseEntity.ok().body(Response.stateOnly(true))
    }

    @PostMapping("/{poll_id}")
    fun selectOption(@PathVariable("poll_id") pollId: Int, @RequestBody pollOption: Int): ResponseEntity<Any> {
        val auth = SecurityContextHolder.getContext().authentication!! as TokenAuthToken

        val optionResult = pollService.increaseOptionCount(auth.getUserData().id!!, pollId, pollOption)

        if (!optionResult) {
            return ResponseEntity.badRequest().body(
                Response.error<Any>("투표에 실패했습니다")
            )
        }

        return ResponseEntity.ok().body(Response.stateOnly(true))
    }

    @GetMapping("/my-poll/{poll_id}")
    @ResponseBody
    fun pollInfo(@PathVariable("poll_id") pollId: Int): ResponseEntity<Any> {
        val optionList = pollService.getPoll(pollId)

        if (optionList.isFailure) {
            return ResponseEntity.badRequest().body(
                Response.error<Any>("Parse Error")
            )
        }
        return ResponseEntity.ok().body(Response.success(optionList.getOrNull()))
    }
}