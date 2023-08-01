package com.tovelop.maphant.service

import com.tovelop.maphant.dto.BookmarkDTO
import com.tovelop.maphant.mapper.BookmarkMapper
import org.springframework.stereotype.Service

@Service
class BookmarkService(val bookmarkMapper: BookmarkMapper) {

    fun insert(userId: Int, boardId: Int): Boolean {
        try {
            bookmarkMapper.insertBoard(userId, boardId)
        } catch (e: Exception) {
            //이미 추가됐거나, 북마크 전에 삭제된 경우
            return false
        }
        return true
    }

    fun showBookmarks(userId: Int): Result<List<BookmarkDTO>> {
        return Result.runCatching { bookmarkMapper.selectBoardAllById(userId) }
    }
}