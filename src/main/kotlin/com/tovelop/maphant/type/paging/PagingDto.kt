package com.tovelop.maphant.type.paging

import jakarta.validation.constraints.Min

data class PagingDto(
    @field:Min(value = 1, message = "page는 1보다 크거나 같아야합니다.")
    var page: Int = 1,
    @field:Min(value = 1, message = "recordSize는 1보다 크거나 같아야합니다.")
    var recordSize: Int = 10,

    var pageSize: Int = 10,
//    var keyword: String? = null,
//    var searchType: String? = null
) {
    val offset: Int
        get() = (page - 1) * recordSize
}
