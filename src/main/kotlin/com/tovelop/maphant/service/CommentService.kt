package com.tovelop.maphant.service

import com.tovelop.maphant.dto.CommentDTO
import com.tovelop.maphant.mapper.CommentMapper
import org.springframework.stereotype.Service

@Service
class CommentService(val commentMapper: CommentMapper) {
    fun findAllComment(boardId: Int) = commentMapper.findAllComment(boardId)

    fun insertComment(commentDTO: CommentDTO) = commentMapper.insertComment(commentDTO)


    fun deleteComment(userId: Int, commentId: Int) = commentMapper.deleteComment(userId, commentId)

    fun updateComment(commentDTO: CommentDTO) = commentMapper.updateComment(commentDTO)

    fun cntComment(boardId: Int) = commentMapper.cntComment(boardId)

    fun insertCommentLike(userId: Int, commentId: Int) = commentMapper.insertCommentLike(userId, commentId)

    fun findCommentLike(userId: Int, commentId: Int) = commentMapper.findCommentLike(userId, commentId)

    fun cntCommentLike(commentId: Int) = commentMapper.cntCommentLike(commentId)

    fun deleteCommentLike(userId: Int, commentId: Int) = commentMapper.deleteCommentLike(userId, commentId)

    fun insertCommentReport(userId: Int, commentId: Int, reportReason: String) =
        commentMapper.insertCommentReport(userId, commentId, reportReason)

    fun findCommentReport(userId: Int, commentId: Int) = commentMapper.findCommentReport(userId, commentId)

    fun cntCommentReport(commentId: Int) = commentMapper.cntCommentReport(commentId)
}