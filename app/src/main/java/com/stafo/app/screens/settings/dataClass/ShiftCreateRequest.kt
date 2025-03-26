package com.stafo.app.screens.settings.dataClass

data class ShiftCreateRequest(
    val shift_name: String,
    val start_time: String,
    val end_time: String
)
