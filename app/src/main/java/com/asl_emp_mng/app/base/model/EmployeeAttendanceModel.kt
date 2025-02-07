package com.asl_emp_mng.app.base.model

data class EmployeeAttendanceModel(
    val checkInOut : String ="",
    val attendTime : String ="",
    val checkType : String ="",
    val attendDate : String ="",
    val attend : Boolean
)
