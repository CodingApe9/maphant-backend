package com.tovelop.maphant.type.response

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletResponse

typealias ResponseUnit = Response<Unit>

private val mapper = ObjectMapper()

fun Response<out Any>.isSuccess(): Boolean {
    return this.success
}

fun HttpServletResponse.writeResponse(response: Response<out Any>) {
    this.contentType = "application/json"
    this.characterEncoding = "UTF-8"
    this.status = if (response.isSuccess()) HttpServletResponse.SC_OK else HttpServletResponse.SC_BAD_REQUEST

    this.writer.write(mapper.writeValueAsString(response))
}