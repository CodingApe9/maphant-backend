package com.tovelop.maphant.service

import com.tovelop.maphant.dto.PollDTO
import com.tovelop.maphant.mapper.PollMapper
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service

@Service
class PollService(val pollMapper: PollMapper) {

    fun increaseOptionCount(userId: Int, pollId: Int, pollOption: Int): Boolean {
        try {
            pollMapper.insertPollUser(userId, pollId, pollOption)
            pollMapper.updateCount(pollOption, pollId)
        } catch (e: Exception) {
            if (e.cause is java.sql.SQLIntegrityConstraintViolationException) {
                val prevOptionId = pollMapper.prevOptionId(userId, pollId)
                pollMapper.decreaseCount(prevOptionId, pollId)
                pollMapper.updatePollUser(userId, pollId, pollOption)
                pollMapper.updateCount(pollOption, pollId)
            } else {
                return false
            }
        }
        return true
    }

    fun createPoll(poll: PollDTO) {
        try {
            pollMapper.insertPoll(poll)
            poll.options.forEach { pollMapper.insertPollOption(poll.id!!, it) }
        } catch (e: Exception) {
            if (e.cause is java.sql.SQLIntegrityConstraintViolationException) {
                throw BadCredentialsException("이미 투표가 생성되어 있습니다.")
            } else {
                e.printStackTrace()
            }
        }
    }

    fun getPollIdByBoardId(boardId: Int): Int {
        return pollMapper.getPollIdByBoardId(boardId)
    }

    fun getPoll(pollId: Int): Result<List<Map<String, Int>>> {
        return Result.runCatching { pollMapper.selectPollInfoById(pollId) }
    }
}