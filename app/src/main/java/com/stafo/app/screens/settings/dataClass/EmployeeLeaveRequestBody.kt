package com.stafo.app.screens.settings.dataClass

data class EmployeeLeaveRequestBody(
    val employee_id:String,
    val from_date:String,
    val to_date:String,
    val reason:String,
    val leave_type:Int
)
