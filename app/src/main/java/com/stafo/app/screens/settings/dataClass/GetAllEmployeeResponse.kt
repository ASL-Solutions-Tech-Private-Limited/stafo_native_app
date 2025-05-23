package com.stafo.app.screens.settings.dataClass

import com.google.gson.annotations.SerializedName

data class GetAllEmployeeResponse(
    val status: Boolean,
    val message: String,
    val data: List<GetEmployee>
)

data class GetEmployee(
    val id: Int,
    val emp_id: String,
    val name: String,
    val email: String,
    val phone: String,
    val position: String,
    val salary: String?,
    @SerializedName("selfie_image") val selfieImage: String?,
    @SerializedName("selfie_image_path") val selfieImagePath: String?,
    val company_id: Int,
    val branch_name: String,
    val department_name: String,
    val geo_status: String?,
    val status: String,
    @SerializedName("device_status") val deviceStatus: String?,
    @SerializedName("attendance_type") val attendanceType: String?,
    val attendances: List<Attendance>,
    val shifts: List<Shift>
)

data class Attendance(
    val attendance: String,
    val halfday: Int,
    val date: String,
    val in_time: String?,
    val out_time: String?
)
data class Shift(
    val id: Int,
    val shift_name: String,
    val start_time: String,
    val end_time: String,
    val company_id: Int,
    val sunday: Int,
    val monday: Int,
    val tuesday: Int,
    val wednesday: Int,
    val thursday: Int,
    val friday: Int,
    val saturday: Int
)


