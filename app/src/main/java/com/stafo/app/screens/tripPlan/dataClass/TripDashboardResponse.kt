package com.stafo.app.screens.tripPlan.dataClass


import com.google.gson.annotations.SerializedName

data class TripDashboardResponse(
    @SerializedName("drivers")
    var driversListData: List<DriversData>?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Boolean?,
    @SerializedName("summary")
    var summaryData: SummaryData?,
    @SerializedName("trips")
    var tripsListData: List<TripsData>?,
    @SerializedName("vehicles")
    var vehiclesListData: List<VehiclesData>?
)

data class CustomerInfo(
    @SerializedName("customer_name")
    var customerName: String?,
    @SerializedName("email")
    var email: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("phone")
    var phone: String?
)

data class SummaryData(
    @SerializedName("total_customers")
    var totalCustomers: Int?,
    @SerializedName("total_drivers")
    var totalDrivers: Int?,
    @SerializedName("total_trips")
    var totalTrips: Int?,
    @SerializedName("total_vehicles")
    var totalVehicles: Int?
)

data class DriversData(
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

data class TripsData(
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
    var vehicleId: Int?,
    @SerializedName("odometer_start")
    var odometer_start: String?,
    @SerializedName("odometer_latest")
    var odometer_latest: String?,
    @SerializedName("total_expenses")
    var total_expenses: String?,
    @SerializedName("duration")
    var duration: String? = "0"
)

data class VehiclesData(
    @SerializedName("company_id")
    var companyId: Int?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("status")
    var status: String?,
    @SerializedName("vehicle_no")
    var vehicleNo: String?,
    @SerializedName("vehicle_type")
    var vehicleType: String?
)