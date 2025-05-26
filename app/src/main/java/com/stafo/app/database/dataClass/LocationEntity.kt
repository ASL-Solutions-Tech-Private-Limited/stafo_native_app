package com.stafo.app.database.dataClass

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_table")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val latitude: String,
    val longitude: String,
    val timestamp: String,
    val isNetwork: Boolean,
    val isGpsTurn: Boolean,
    val deviceName: String = "",
    val batteryPercentage: Int = 0,
    val androidVersion: String = ""
)
