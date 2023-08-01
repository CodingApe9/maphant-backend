package com.tovelop.maphant.type.paging.dm

import com.tovelop.maphant.type.paging.Pagination
import com.tovelop.maphant.type.paging.PagingResponse

class DmPagingResponse<T>(
    other_id: Int,
    other_nickname: String,
    list: List<T> = mutableListOf(),
    pagination: Pagination? = null
) : PagingResponse<T>(list, pagination) {
    // 새로운 클래스의 멤버들
    val other_id = other_id
    val other_nickname = other_nickname
}
