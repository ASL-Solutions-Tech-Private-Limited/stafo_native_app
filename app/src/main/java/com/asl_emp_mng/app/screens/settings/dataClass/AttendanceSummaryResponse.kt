package com.asl_emp_mng.app.screens.settings.dataClass

import com.google.gson.annotations.SerializedName

data class AttendanceSummaryResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("employeeCount") val employeeCount: Int,
    @SerializedName("presentCount") val presentCount: Int,
    @SerializedName("company_id") val companyId: Int,
    @SerializedName("employeesOnLeave") val employeesOnLeave: ArrayList<Leave>?,
    @SerializedName("birthday") val birthday: ArrayList<Birthday>?,
    @SerializedName("anniversary") val anniversary: ArrayList<Annyversary>?
)


data class Birthday(
    @SerializedName("id") val id: Int,
    @SerializedName("emp_id") val emp_id: String,
    @SerializedName("date_of_birth") val date_of_birth: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("image") val image: String
)

data class Annyversary(
    @SerializedName("id") val id: Int,
    @SerializedName("emp_id") val emp_id: String,
    @SerializedName("date_of_joining") val date_of_joining: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("image") val image: String
)
