package com.tovelop.maphant.type.paging

data class Pagination(
    var totalRecordCount: Int = 0,        // 전체 데이터 수
    var totalPageCount: Int = 0,          // 전체 페이지 수
    var startPage: Int = 0,               // 첫 페이지 번호
    var endPage: Int = 0,                 // 끝 페이지 번호
    var limitStart: Int = 0,              // LIMIT 시작 위치
    var existPrevPage: Boolean = false,   // 이전 페이지 존재 여부
    var existNextPage: Boolean = false    // 다음 페이지 존재 여부
) {
    constructor(totalRecordCount: Int, params: PagingDto) : this() {
        if (totalRecordCount > 0) {
            this.totalRecordCount = totalRecordCount
            calculation(params)
        }
    }

    private fun calculation(params: PagingDto) {
        // 전체 페이지 수 계산
        totalPageCount = ((totalRecordCount - 1) / params.recordSize) + 1

        // 현재 페이지 번호가 전체 페이지 수보다 큰 경우, 현재 페이지 번호에 전체 페이지 수 저장
        if (params.page > totalPageCount) {
            params.page = totalPageCount
        }

        // 첫 페이지 번호 계산
        startPage = ((params.page - 1) / params.pageSize) * params.pageSize + 1

        // 끝 페이지 번호 계산
        endPage = startPage + params.pageSize - 1

        // 끝 페이지가 전체 페이지 수보다 큰 경우, 끝 페이지에 전체 페이지 수 저장
        if (endPage > totalPageCount) {
            endPage = totalPageCount
        }

        // LIMIT 시작 위치 계산
        limitStart = (params.page - 1) * params.recordSize

        // 이전 페이지 존재 여부 확인
        existPrevPage = startPage != 1

        // 다음 페이지 존재 여부 확인
        existNextPage = (endPage * params.recordSize) < totalRecordCount
    }
}