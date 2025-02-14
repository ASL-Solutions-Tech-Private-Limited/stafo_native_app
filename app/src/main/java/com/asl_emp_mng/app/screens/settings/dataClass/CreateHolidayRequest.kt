package com.asl_emp_mng.app.screens.settings.dataClass


data class CreateHolidayRequest(
    val holidays: List<HolidayPostData>
)


data class HolidayPostData(
    val title: String,
    val description: String,
    val start_date: String,
    val end_date: String
)