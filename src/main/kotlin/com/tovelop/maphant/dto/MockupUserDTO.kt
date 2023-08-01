package com.tovelop.maphant.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class MockupUserDTO @JsonCreator constructor(
    @JsonProperty("id") val id: Int,
    @JsonProperty("email") val email: String,
    @JsonProperty("password") var password: String,
    @JsonProperty("name") val name: String,
) {
    constructor(): this(0, "", "", "")
}