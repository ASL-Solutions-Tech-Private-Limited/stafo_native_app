package com.stafo.app.base.model

data class DynamicField(
    val hint: String,
    val options: List<String>,
    var userInput: String = "",
    var selectedOption: String = ""
)

