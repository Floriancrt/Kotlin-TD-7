package com.example.tdn2

import androidx.annotation.Nullable
import java.io.Serializable

data class Task(val id : String, val title : String, val description : String = "") : Serializable