package com.tovelop.maphant.mapper

import com.tovelop.maphant.dto.AdminBoardReportDTO
import com.tovelop.maphant.dto.BoardReportInfoDTO
import com.tovelop.maphant.dto.UserDTO
import org.apache.ibatis.annotations.Mapper
import org.springframework.stereotype.Repository

@Mapper
@Repository
interface AdminPageMapper {
    fun findBoardReportByReportedAt(reportSize: Int): List<AdminBoardReportDTO>
    fun findBoardReportByMostReportedRanking(reportSize: Int): List<AdminBoardReportDTO>
    fun findBoardReportBySortType(reportSize: Int, sortType: String): List<AdminBoardReportDTO>
    fun findReportInfo(boardId: Int): List<BoardReportInfoDTO>

//    fun setBoardSanction(boardId: Int)
//    fun findCommentReport()
//    fun setCommentSanction(commentId: Int)
//    fun findUserSanction()
//    fun setUserSanction(userId: Int)
}
