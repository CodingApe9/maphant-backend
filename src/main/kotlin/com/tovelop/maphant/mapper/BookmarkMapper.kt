package com.tovelop.maphant.mapper

import com.tovelop.maphant.dto.BookmarkDTO
import org.apache.ibatis.annotations.Mapper
import org.springframework.stereotype.Repository

@Mapper
@Repository
interface BookmarkMapper {

    fun insertBoard(userId: Int, boardId: Int)

    fun selectBoardAllById(userId: Int): List<BookmarkDTO>
}