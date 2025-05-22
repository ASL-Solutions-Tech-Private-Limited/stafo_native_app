package com.stafo.app.database.dataClass

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_table")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val latitude: String,
    val longitude: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false,
    val deviceName: String,
    val batteryPercentage: Int,
    val androidVersion: String
)
