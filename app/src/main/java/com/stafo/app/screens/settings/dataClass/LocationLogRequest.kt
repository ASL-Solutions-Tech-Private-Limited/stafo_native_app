package com.stafo.app.screens.settings.dataClass

import com.stafo.app.database.dataClass.LocationEntity

data class LocationLogRequest(
    val company_id: String,
    val employee_id: String,
    val log_data: List<LocationEntity>
)