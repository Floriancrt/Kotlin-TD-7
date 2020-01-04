package com.example.tdn2

import com.squareup.moshi.Json

data class TaskInfo (
    @field:Json(name = "title")
    val title: String,
    @field:Json(name = "description")
    val description: String
)