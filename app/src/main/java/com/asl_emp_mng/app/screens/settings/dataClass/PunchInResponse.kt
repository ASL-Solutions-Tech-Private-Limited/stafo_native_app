package com.asl_emp_mng.app.screens.settings.dataClass
import com.google.gson.annotations.SerializedName

data class PunchInResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: PunchInData
)

data class PunchInData(
    @SerializedName("employee_id") val employeeId: Int,
    @SerializedName("punch_in") val punchIn: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("id") val id: Int
)
