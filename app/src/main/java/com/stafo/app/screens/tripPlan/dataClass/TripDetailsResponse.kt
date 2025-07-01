package com.stafo.app.screens.tripPlan.dataClass

data class TripDetailsResponse(
    val status: Boolean,
    val message: String,
    val trip: Trip
) {
    data class Trip(
        val id: Int,
        val title: String,
        val vehicle_id: Int,
        val driver_id: Int,
        val company_id: Int,
        val customer_id: Int,
        val start_latitude: String,
        val start_longitude: String,
        val end_latitude: String,
        val end_longitude: String,
        val from_address: String,
        val to_address: String,
        val distance: Double,
        val start_time: String,
        val end_time: String,
        val notes: String,
        val status: String,
        val customer_info: CustomerInfo,
        val driver: Driver,
        val vehicle: Vehicle,
        val trip_logs: ArrayList<TripLog>
    ) {
        data class CustomerInfo(
            val id: Int,
            val customer_name: String,
            val email: String,
            val phone: String,
            val address: String
        )

        data class Driver(
            val id: Int,
            val name: String,
            val emp_id: String,
            val position: String
        )

        data class Vehicle(
            val id: Int,
            val vehicle_no: String,
            val vehicle_type: String,
            val fuel: String,
            val load_capacity: String,
            val speedometer: Int,
            val status: String,
            val km_travelled: Int
        )

        data class TripLog(
            val id: Int,
            val trip_id: Int,
            val action_type: String,
            val latitude: String,
            val longitude: String,
            val speed: Double?, // Nullable since it's `null` in JSON
            val odometer: String,
            val image_path: String,
            val timestamp: String,
            val created_at: String,
            val updated_at: String
        )
    }
}