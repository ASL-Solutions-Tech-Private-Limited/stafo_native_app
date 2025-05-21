package com.stafo.app.screens.settings.dataClass

import com.google.gson.annotations.SerializedName

data class AttendanceSummaryResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("employeeCount") val employeeCount: Int,
    @SerializedName("maxEmployeeAdd") val maxEmployeeAdd: String,
    @SerializedName("presentCount") val presentCount: Int,
    @SerializedName("company_id") val companyId: Int,
    @SerializedName("employeesOnLeave") val employeesOnLeave: ArrayList<Leave>?,
    @SerializedName("birthday") val birthday: ArrayList<Birthday>?,
    @SerializedName("anniversary") val anniversary: ArrayList<Annyversary>?,
    @SerializedName("companyInfo") val companyInfo: VerifyCompanyInfo?
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
data class VerifyCompanyInfo(
    val id: Int?,
    @SerializedName("company_name") val companyName: String?,
    @SerializedName("company_code") val companyCode: String?,
    @SerializedName("is_verified") val isVerified: String?,
    @SerializedName("max_employee_add") val maxEmployeeAdd:Int,
)