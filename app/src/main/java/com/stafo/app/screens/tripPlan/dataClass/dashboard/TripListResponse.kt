package com.stafo.app.screens.tripPlan.dataClass.dashboard


import com.google.gson.annotations.SerializedName
import com.stafo.app.screens.tripPlan.dataClass.CustomerInfo

data class TripListResponse(
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Boolean?,
    @SerializedName("trips")
    var tripsList: List<Trips>?
)

data class Trips(
    @SerializedName("company_id")
    var companyId: Int?,
    @SerializedName("customer_id")
    var customerId: Int?,
    @SerializedName("customer_info")
    var customerInfo: CustomerInfo?,
    @SerializedName("distance")
    var distance: Double?,
    @SerializedName("driver_id")
    var driverId: Int?,
    @SerializedName("end_latitude")
    var endLatitude: String?,
    @SerializedName("end_longitude")
    var endLongitude: String?,
    @SerializedName("end_time")
    var endTime: String?,
    @SerializedName("from_address")
    var fromAddress: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("notes")
    var notes: String?,
    @SerializedName("start_latitude")
    var startLatitude: String?,
    @SerializedName("start_longitude")
    var startLongitude: String?,
    @SerializedName("start_time")
    var startTime: String?,
    @SerializedName("status")
    var status: String?,
    @SerializedName("title")
    var title: String?,
    @SerializedName("to_address")
    var toAddress: String?,
    @SerializedName("vehicle_id")
    var vehicleId: Int?
)