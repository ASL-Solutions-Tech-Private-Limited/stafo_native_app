package com.asl_emp_mng.app.base.model

data class DateItem(
    val date: String,
    var isPresent: String = "",
    var punchIn: String="",
    var punchOut: String=""
)

