package com.stafo.app.screens.tripPlan.dataClass


import com.google.gson.annotations.SerializedName

data class DriverListResponse(
    @SerializedName("drivers")
    var driversList: List<Drivers>?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Boolean?
)

data class Drivers(
    @SerializedName("company_id")
    var companyId: Int?,
    @SerializedName("emp_id")
    var empId: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("position")
    var position: String?
)