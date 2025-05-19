package com.stafo.app.base.model

data class DateItem(
    val date: String,
    var isPresent: String,
    var punchIn: String,
    var punchOut: String,
    val isPlaceholder: Boolean = false
)

