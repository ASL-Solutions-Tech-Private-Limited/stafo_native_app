package com.stafo.app.base.model

import com.google.gson.annotations.SerializedName

data class EmployeeAttendanceModel(
    val id: Int,
    val attendance: String,
    val halfday: Int,
    val date: String,
    val in_time: String,
    val out_time: String,
    @SerializedName("punchin_image") val punchInImage: String?,
    @SerializedName("punchout_image") val punchOutImage: String?
)


