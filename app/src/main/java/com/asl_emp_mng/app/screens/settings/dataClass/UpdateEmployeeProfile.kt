package com.asl_emp_mng.app.screens.settings.dataClass
import com.google.gson.annotations.SerializedName

data class UpdateEmployeeProfile(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("position") val position: String,
    @SerializedName("salary") val salary: Int,
    @SerializedName("branch_id") val branchId: Int,
    @SerializedName("department_id") val departmentId: Int,
    @SerializedName("marital_status") val maritalStatus: String,
    @SerializedName("guardian_name") val guardianName: String,
    @SerializedName("blood_group") val bloodGroup: String,
    @SerializedName("date_of_joining") val dateOfJoining: String,
    @SerializedName("date_of_birth") val dateOfBirth: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("address") val address: String
)
