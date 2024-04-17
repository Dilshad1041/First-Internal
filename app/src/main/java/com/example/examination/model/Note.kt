package com.example.examination.model

import java.util.Date

data class Note(
    val title: String = "", // Change "noteTitle" to "title"
    val note: String = "",
    var timestamp: Any? = null // Use Date type for timestamp
)



