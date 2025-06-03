package com.stafo.app.screens.tripPlan.dataClass


import com.google.gson.annotations.SerializedName

data class CreateTripResponse(
    @SerializedName("customer")
    var customerDeX: CustomerDeX?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Boolean?,
    @SerializedName("trip")
    var tripDeX: TripDeX?
)

data class TripDeX(
    @SerializedName("company_id")
    var companyId: Int?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("customer_id")
    var customerId: Int?,
    @SerializedName("distance")
    var distance: Double?,
    @SerializedName("driver_id")
    var driverId: String?,
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
    @SerializedName("updated_at")
    var updatedAt: String?,
    @SerializedName("vehicle_id")
    var vehicleId: String?
)

data class CustomerDeX(
    @SerializedName("address")
    var address: String?,
    @SerializedName("company_id")
    var companyId: Int?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("customer_name")
    var customerName: String?,
    @SerializedName("email")
    var email: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("phone")
    var phone: String?,
    @SerializedName("updated_at")
    var updatedAt: String?
)