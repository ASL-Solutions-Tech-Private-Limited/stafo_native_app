package com.asl_emp_mng.app.base.model

data class EmployeeAttendanceModel(

    val attendance: String,
    val halfday: Int,
    val date: String,
    val in_time: String,
    val out_time: String
)


