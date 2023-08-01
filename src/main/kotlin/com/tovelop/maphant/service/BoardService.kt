package com.tovelop.maphant.service

import com.tovelop.maphant.configure.security.token.TokenAuthToken
import com.tovelop.maphant.dto.*
import com.tovelop.maphant.mapper.BoardMapper
import com.tovelop.maphant.type.response.Response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service


@Service
class BoardService(@Autowired val boardMapper: BoardMapper) {
    fun getBoardTypeIdByBoardTypeName(boardTypeName: String): Int {
        return boardMapper.getBoardTypeIdByBoardTypeName(boardTypeName)
    }
    fun getCategoryIdByCategoryName(categoryName: String): Int {
        return boardMapper.getCategoryIdByCategoryName(categoryName)
    }

    fun findBoardList(findBoardDTO: FindBoardDTO, userId: Int, categoryId: Int): List<PageBoardDTO> {
        val startRow = (findBoardDTO.page - 1) * findBoardDTO.pageSize
        val boardTypeId = boardMapper.getBoardTypeIdByBoardTypeName(findBoardDTO.boardType)
        return boardMapper.findBoardList(findBoardDTO, startRow, categoryId, boardTypeId);
    }
    fun insertBoard(boardDTO: BoardDTO) {
        boardMapper.insertBoard(boardDTO)
    }

    fun findBoard(boardId: Int, userId: Int): ExtBoardDTO? {
        return boardMapper.findBoard(boardId)?.toExtBoardDTO(findBoardLike(boardId, userId))
    }

    fun updateBoard(updateBoardDTO: UpdateBoardDTO) {
        boardMapper.updateBoard(updateBoardDTO)
    }

    fun deleteBoard(boardId: Int) {
        boardMapper.deleteBoard(boardId)
    }

    fun getIsHideByBoardId(boardId: Int): Boolean? {
        val board = boardMapper.findBoard(boardId)
        return board?.isHide==1
    }

    fun getUserIdByBoardId(boardId: Int): Int?{
        val board = boardMapper.findBoard(boardId)
        return board?.userId
    }
    fun isModified(boardId: Int): Boolean {
        val board = boardMapper.findBoard(boardId)
        return board?.modifiedAt != null
    }
    fun insertBoardLike(boardId: Int, userId: Int) {
        boardMapper.insertBoardLike(boardId, userId)
    }
    fun deleteBoardLike(boardId: Int, userId: Int) {
        boardMapper.deleteBoardLike(boardId, userId)
    }
    fun insertBoardReport(boardId: Int, userId: Int, reportId: Int) {
        boardMapper.insertBoardReport(boardId, userId, reportId)
    }
    fun findBoardByKeyword(keyword: String): List<BoardDTO> {
        return boardMapper.findBoardByKeyword(keyword)
    }
    fun isInCategory(categoryId: Int): Boolean {
        return boardMapper.isInCategory(categoryId) != null
    }
    fun isInBoardtype(boardType: String): Boolean {
        return boardMapper.isInBoardtype(boardType) != null
    }
    fun findBoardLike(boardId: Int, userId: Int): Boolean{
        val boardLikeDTO = boardMapper.findBoardLike(boardId, userId)
        return boardLikeDTO!=null
    }
    fun isInReportByBoardId(boardId: Int, userId: Int): Boolean{
        val boardReportDTO = boardMapper.isInReportByBoardId(boardId, userId)
        return boardReportDTO!=null
    }
    fun isInReportId(reportId: Int): Boolean{
        val reportName = boardMapper.isInReportId(reportId)
        return !reportName.isNullOrBlank()
    }
    fun isInBoardByBoardId(boardId: Int): Boolean{
        val isInboardId = boardMapper.isInBoardByBoardId(boardId)
        return isInboardId!=null
    }
    fun completeBoard(parentBoardId: Int, childBoardId: Int, userId: Int){
        boardMapper.insertBoardQna(parentBoardId, childBoardId)
        boardMapper.updateIsCompleteOfBoard(parentBoardId, 1)
    }
    fun isinCompleteByBoardId(boardId: Int): Boolean {
        val board = boardMapper.findBoard(boardId)
        return board?.isComplete==1
    }
    fun isParent(parentBoardId: Int, childBoardId: Int): Boolean{
        val childBoard = boardMapper.findBoard(childBoardId)
        return childBoard?.parentId==parentBoardId
    }
}


