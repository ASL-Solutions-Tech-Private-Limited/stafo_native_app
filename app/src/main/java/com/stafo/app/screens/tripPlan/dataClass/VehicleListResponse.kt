package com.stafo.app.screens.tripPlan.dataClass


import com.google.gson.annotations.SerializedName

data class VehicleListResponse(
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Boolean?,
    @SerializedName("vehicles")
    var vehiclesList: List<Vehicles>?
)

data class Vehicles(
    @SerializedName("company_id")
    var companyId: Int?,
    @SerializedName("fuel")
    var fuel: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("km_travelled")
    var kmTravelled: Int?,
    @SerializedName("load_capacity")
    var loadCapacity: String?,
    @SerializedName("rc_upload_path")
    var rcUploadPath: String?,
    @SerializedName("speedometer")
    var speedometer: Int?,
    @SerializedName("status")
    var status: String?,
    @SerializedName("vehicle_no")
    var vehicleNo: String?,
    @SerializedName("vehicle_type")
    var vehicleType: String?,
    @SerializedName("rc_number")
    var rcNumber: String?
)