package com.tovelop.maphant.mapper

import com.tovelop.maphant.dto.PollDTO
import org.apache.ibatis.annotations.Mapper
import org.springframework.stereotype.Repository

@Mapper
@Repository
interface PollMapper {

    //    fun insertPoll(id: Int?, boardId: Int, title: String, expireDateTime: LocalDateTime?): Int
    fun insertPoll(poll: PollDTO)

    fun insertPollOption(pollId: Int, option: String)

    fun insertPollUser(userId: Int, pollId: Int, pollOption: Int)

    fun updatePollUser(userId: Int, pollId: Int, pollOption: Int)

    fun prevOptionId(userId: Int, pollId: Int): Int

    fun decreaseCount(optionId: Int, pollId: Int)

    fun updateCount(optionId: Int, pollId: Int)

    fun getPollIdByBoardId(boardId: Int): Int

    fun selectPollInfoById(pollId: Int): List<Map<String, Int>>

}