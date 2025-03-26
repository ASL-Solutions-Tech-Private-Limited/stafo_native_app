package com.stafo.app.base.model

data class DynamicHolidayField(
    val hint: String,
    val hint2: String,
    val hint3: String,
    var userInput: String = "",
    var userInput2: String = "",
    var userInput3: String = "",
)
