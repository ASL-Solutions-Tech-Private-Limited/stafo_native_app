package com.stafo.app.screens.settings.dataClass

import com.google.gson.annotations.SerializedName
import com.stafo.app.base.model.EmployeeAttendanceModel

data class EmployeeListResponse(
    val status: Boolean,
    val message: String,
    val data: List<EmployeeDataList>
)


data class EmployeeDataList(
    val id: Int,
    val emp_id: String,
    val name: String,
    val email: String,
    val phone: String,
    val position: String? = null,
    val salary: String? = null,
    val image: String?,
    @SerializedName("image_path") val imagePath: String,
    @SerializedName("selfie_image") val selfieImage: String?,
    @SerializedName("selfie_image_path") val selfieImagePath: String,
    val company_id: Int,
    val branch_name: String,
    val department_name: String,
    val last_punch_in: String? = null,
    val last_punch_out: String? = null,
    val geo_status: String? = null,
    val attendances: List<EmployeeAttendanceModel>
)




