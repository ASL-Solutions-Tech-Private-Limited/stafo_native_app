package com.stafo.app.screens.settings.dataClass

data class OnLeaveResponse(
    val leave: List<Leave>
)

data class Leave(
    val id: Int,
    val employee_id: Int,
    val from_date: String,
    val to_date: String,
    val reason: String,
    val leave_type: String? = null,
    val employee_basic_info: OnLeaveEmployeeBasicInfo
)

data class OnLeaveEmployeeBasicInfo(
    val id: Int,
    val emp_id: String,
    val name: String,
    val email: String,
    val phone: String
)
