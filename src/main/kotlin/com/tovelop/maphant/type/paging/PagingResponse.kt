package com.tovelop.maphant.type.paging

open class PagingResponse<T>(
    val list: List<T> = mutableListOf(),
    val pagination: Pagination?
)