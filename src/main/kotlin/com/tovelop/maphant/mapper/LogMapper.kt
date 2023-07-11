package com.tovelop.maphant.mapper

import org.apache.ibatis.annotations.Mapper
import org.springframework.stereotype.Repository

@Mapper
@Repository
interface LogMapper {
    // logDTO 생성시 인자를 logDTO로 받기
    fun insertLog()
}