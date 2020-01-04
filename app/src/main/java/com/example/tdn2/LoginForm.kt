package com.example.tdn2

import com.squareup.moshi.Json

data class LoginForm (
    @field:Json(name = "email")
    val email: String,
    @field:Json(name = "password")
    val password: String
)