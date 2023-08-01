package com.tovelop.maphant.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class UserDataDTO @JsonCreator constructor(
    @JsonProperty("id") val id: Int,
    @JsonProperty("email") val email: String,
    @JsonProperty("password") var password: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("nickname") val nickname: String,
    @JsonProperty("role") val role: String,
    @JsonProperty("categoryId") val categoryId: Int,
    @JsonProperty("majorId") val majorId: Int,
) {
    constructor(): this(0, "", "", "", "", "",0, 0)
}
