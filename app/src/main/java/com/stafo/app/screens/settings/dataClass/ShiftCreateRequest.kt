package com.stafo.app.screens.settings.dataClass

data class ShiftCreateRequest(
    val shift_name: String,
    val start_time: String,
    val end_time: String,
    val sunday: Boolean,
    val monday: Boolean,
    val tuesday: Boolean,
    val wednesday: Boolean,
    val thursday: Boolean,
    val friday: Boolean,
    val saturday: Boolean
)
