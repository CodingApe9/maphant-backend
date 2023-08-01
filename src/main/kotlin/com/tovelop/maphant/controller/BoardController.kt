package com.tovelop.maphant.controller


import com.tovelop.maphant.dto.*
import com.tovelop.maphant.configure.security.token.TokenAuthToken
import com.tovelop.maphant.service.BoardService
import com.tovelop.maphant.type.response.Response
import com.tovelop.maphant.type.response.ResponseUnit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/board")
class BoardController(@Autowired val boardService: BoardService) {
    val categoryMap = mapOf("createdAt" to "created_at", "likeCnt" to "like_cnt")

    @PostMapping("/main")
    fun readBoardList(
        @RequestBody findBoardDTO: FindBoardDTO
    ): ResponseEntity<Any> {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth == null || auth !is TokenAuthToken || !auth.isAuthenticated) {
            return ResponseEntity.badRequest().body(Response.error<Any>("로그인 안됨"))
        }
        //pageNum과 pageSize는 양의 정수
        if (findBoardDTO.page <= 0) {
            return ResponseEntity.badRequest().body(Response.error<Any>("pageNum가 일치하지 않습니다."))
        }
        if (findBoardDTO.pageSize <= 0) {
            return ResponseEntity.badRequest().body(Response.error<Any>("pageSize가 일치하지 않습니다."))
        }
        if (findBoardDTO.sortCriterion !in categoryMap.keys) {
            // sortStandard 값이 유효하지 않은 경우
            return ResponseEntity.badRequest().body(Response.error<Any>("유효하지 않은 sortCriterion 값입니다."))
        }
        if (!boardService.isInCategory(auth.getUserData().categoryId) || !boardService.isInBoardtype(findBoardDTO.boardType)) {
            // 클라이언트가 존재하지 않는 카테고리나 게시판 유형을 요청한 경우
            return ResponseEntity.badRequest().body(Response.error<Any>("존재하지 않는 카테고리나 게시판 유형입니다."))
        }
        val boardList = boardService.findBoardList(findBoardDTO,auth.getUserData().id,auth.getUserData().categoryId)
        return if (boardList.isEmpty()) {
            ResponseEntity.badRequest().body(Response.error<Any>("요청에 실패했습니다."))
        } else {
            ResponseEntity.ok().body(Response.success(boardList))
        }
    }

    @PostMapping("/like/{boardId}")
    fun insertLikePost(@PathVariable("boardId") boardId: Int): ResponseEntity<Any> {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth == null || auth !is TokenAuthToken || !auth.isAuthenticated) {
            return ResponseEntity.badRequest().body(Response.error<Any>("로그인 안됨"))
        }
        val board = boardService.findBoard(boardId,auth.getUserData().id)
        if (board == null) {
            return ResponseEntity.badRequest().body(Response.error<Any>("게시글이 존재하지 않습니다."))
        }
        boardService.insertBoardLike(boardId, auth.getUserData().id)
        return ResponseEntity.ok(Response.stateOnly(true))
    }

    @DeleteMapping("/like/{boardId}")
    fun deleteLikeBoard(@PathVariable("boardId") boardId: Int): ResponseEntity<Any> {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth == null || auth !is TokenAuthToken || !auth.isAuthenticated) {
            return ResponseEntity.badRequest().body(Response.error<Any>("로그인 안됨"))
        }
        val board = boardService.findBoard(boardId,auth.getUserData().id)
        if (board == null) {
            return ResponseEntity.badRequest().body(Response.error<Any>("게시글이 존재하지 않습니다."))
        }
        boardService.deleteBoardLike(boardId, board.userId)
        return ResponseEntity.ok(Response.stateOnly(true))
    }

    @GetMapping("/{boardId}")
    fun readBoard(@PathVariable("boardId") boardId: Int): ResponseEntity<Any> {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth == null || auth !is TokenAuthToken || !auth.isAuthenticated) {
            return ResponseEntity.badRequest().body(Response.error<Any>("로그인 안됨"))
        }
        val board = boardService.findBoard(boardId,auth.getUserData().id)
        if (board == null || boardService.getIsHideByBoardId(boardId) == null) {
            return ResponseEntity.badRequest().body(Response.error<Any>("게시글이 존재하지 않습니다."))
        }
        if (boardService.getIsHideByBoardId(boardId)!!) {
            if (board.userId != auth.getUserData().id) {
                return ResponseEntity.badRequest().body(Response.error<Any>("권한이 없습니다."))
            }
        }
        return ResponseEntity.ok(Response.success(board))
    }

    @DeleteMapping("/{boardId}")
    fun deleteBoard(@PathVariable("boardId") boardId: Int): ResponseEntity<Any> {
        // 게시글 삭제
        val auth = SecurityContextHolder.getContext().authentication
        if (auth == null || auth !is TokenAuthToken || !auth.isAuthenticated) {
            return ResponseEntity.badRequest().body(Response.error<Any>("로그인 안됨"))
        }
        // 관리자 권한 확인(관리자는 모든 게시글 삭제 가능)
        // 본인 게시글 인지 확인
        if (auth.getUserData().role != "admin" || auth.getUserData().id != boardService.getUserIdByBoardId(boardId)) {
            return ResponseEntity.badRequest().body(Response.error<Any>("권한이 없습니다."))
        }
        boardService.deleteBoard(boardId)
        return ResponseEntity.ok(Response.stateOnly(true))
    }

    @PostMapping("/create")
    fun createBoard(@RequestBody board: SetBoardDTO): ResponseEntity<ResponseUnit> {
        // 제목 내용 빈칸인지 확인
        return if (board.title.isNotBlank() && board.body.isNotBlank()) {
            boardService.insertBoard(board.toBoardDTO())
            ResponseEntity.ok(Response.stateOnly(true))
        } else {
            ResponseEntity.ok(Response.stateOnly(false))
            // 제목 또는 내용이 빈칸인 경우 실패 응답을 반환합니다.
        }
    }

    @PutMapping("/update")
    fun updateBoard(@RequestBody board: UpdateBoardDTO): ResponseEntity<ResponseUnit> {
        // 현재 로그인한 사용자 정보 가져오기
        val auth = SecurityContextHolder.getContext().authentication
        if (auth == null || auth !is TokenAuthToken || !auth.isAuthenticated) {
            return ResponseEntity.badRequest().body(Response.error<Unit>("로그인 안됨"))
        }
        // 게시글이 존재하지 않는 경우
        val reBoard = boardService.findBoard(board.id,auth.getUserData().id)
            ?: return ResponseEntity.badRequest().body(Response.error<Unit>("게시글이 존재하지 않습니다."))
        // 제목 및 내용 빈칸 확인
        if (reBoard.isComplete == 1) {
            return ResponseEntity.badRequest().body(Response.error<Unit>("채택된 글은 수정이 불가합니다."))
        }
        if (board.title.isEmpty() || board.body.isEmpty()) {
            return ResponseEntity.badRequest().body(Response.error<Unit>("제목과 내용을 입력해주세요."))
        }
        // 본인 게시글 확인
        if (reBoard.userId != auth.getUserData().id) {
            return ResponseEntity.badRequest().body(Response.error<Unit>("권한이 없습니다."))
        }
        // 게시글 읽어오기
        boardService.updateBoard(board)
        return ResponseEntity.ok(Response.stateOnly(true))
    }

    @GetMapping("/search")
    fun searchBoard(@RequestParam content:String): Any {
        val searchBoard = boardService.findBoardByKeyword(content)
        // 검색어가 포함된 게시글 읽어오기
        if(searchBoard.isEmpty()){
            return ResponseEntity.badRequest().body(Response.error<Unit>("검색 결과가 없습니다."))
        }
        // return: json
        return ResponseEntity.ok(Response.success(searchBoard))
    }
   @PostMapping("/report")
    fun reportBoard(@RequestParam boardId: Int,@RequestParam reportId: Int): ResponseEntity<ResponseUnit> {
        val auth = SecurityContextHolder.getContext().authentication
        if(auth == null || auth !is TokenAuthToken || !auth.isAuthenticated){
            return ResponseEntity.badRequest().body(Response.error<Unit>("로그인 안됨"))
        }
        if(!boardService.isInBoardByBoardId(boardId)){
            return ResponseEntity.badRequest().body(Response.error<Unit>("게시글이 존재하지 않습니다."))
        }
        if(auth.getUserData().id == boardService.getUserIdByBoardId(boardId)){
            return ResponseEntity.badRequest().body(Response.error<Unit>("자신의 게시글은 신고할 수 없습니다."))
        }
        if(!boardService.isInReportId(reportId)){
            return ResponseEntity.badRequest().body(Response.error<Unit>("없는 신고 유형입니다."))
        }
        if(boardService.isInReportByBoardId(boardId,auth.getUserData().id)){
            return ResponseEntity.badRequest().body(Response.error<Unit>("이미 신고한 게시글입니다."))
        }
        // 신고하기
        boardService.insertBoardReport(boardId,auth.getUserData().id,reportId)
        // return: json
        return ResponseEntity.ok(Response.stateOnly(true))
    }
    @PostMapping("/complete")
    fun completeBoard(@RequestParam questId: Int,@RequestParam answerId: Int): ResponseEntity<ResponseUnit> {
        val auth = SecurityContextHolder.getContext().authentication
        if(auth == null || auth !is TokenAuthToken || !auth.isAuthenticated){
            return ResponseEntity.badRequest().body(Response.error<Unit>("로그인 안됨"))
        }
        if(!boardService.isInBoardByBoardId(questId) || !boardService.isInBoardByBoardId(answerId)){
            return ResponseEntity.badRequest().body(Response.error<Unit>("게시글이 존재하지 않습니다."))
        }
        if(boardService.getUserIdByBoardId(questId) != auth.getUserData().id){
            return ResponseEntity.badRequest().body(Response.error<Unit>("자신의 게시글이 아닙니다."))
        }
        if(auth.getUserData().id == boardService.getUserIdByBoardId(answerId)){
            return ResponseEntity.badRequest().body(Response.error<Unit>("자신의 게시글은 체택할 수 없습니다."))
        }
        if(boardService.isinCompleteByBoardId(answerId)){
            return ResponseEntity.badRequest().body(Response.error<Unit>("이미 체택한 게시글입니다."))
        }
        if(!boardService.isParent(questId,answerId)){
            return ResponseEntity.badRequest().body(Response.error<Unit>("해당글의 답변이 아닙니다."))
        }

        // 채택하기
        boardService.completeBoard(questId,answerId,auth.getUserData().id)
        // return: json
        return ResponseEntity.ok(Response.stateOnly(true))
    }
}