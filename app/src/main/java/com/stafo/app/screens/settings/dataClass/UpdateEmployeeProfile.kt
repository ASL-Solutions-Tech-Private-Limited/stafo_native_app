package com.stafo.app.screens.settings.dataClass
import com.google.gson.annotations.SerializedName

data class UpdateEmployeeProfile(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("position") val position: String,
    @SerializedName("salary") val salary: Int?,
    @SerializedName("marital_status") val maritalStatus: String,
    @SerializedName("guardian_name") val guardianName: String,
    @SerializedName("blood_group") val bloodGroup: String,
    @SerializedName("date_of_joining") val dateOfJoining: String,
    @SerializedName("date_of_birth") val dateOfBirth: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("address") val address: String,
    @SerializedName("country") val country: Int?,
    @SerializedName("state") val state: Int?,
    @SerializedName("city") val city: Int?,
    @SerializedName("job_title_id") val job_title_id: Int?,
    @SerializedName("employee_type_id") val employee_type_id: Int?,
    @SerializedName("official_email_id") val official_email_id: String,
    @SerializedName("pf_number") val pf_number: String,
    @SerializedName("esi_number") val esi_number: String,
    @SerializedName("date_of_leaving") val date_of_leaving: String
)
