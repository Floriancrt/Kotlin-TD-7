package com.example.tdn2

import com.squareup.moshi.Json

data class UserInfo (
    @field:Json(name = "email")
    val email: String,
    @field:Json(name="firstname")
    val firstname: String,
    @field:Json(name = "lastname")
    val lastname: String,
    @field:Json(name = "avatar")
    val avatar: String
)