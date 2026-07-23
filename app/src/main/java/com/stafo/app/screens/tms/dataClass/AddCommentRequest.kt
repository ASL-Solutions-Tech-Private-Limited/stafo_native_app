package com.stafo.app.screens.tms.dataClass

data class AddCommentRequest(
    val task_id:Int,
    val employee_id: Int? = null,
    val company_id: Int? = null,
    val comments:String
)
