package com.stafo.app.screens.bbps.dataClasses

import com.stafo.app.screens.bbps.BBPSDashboard

// BBPSEntry.kt
sealed class BBPSEntry {
    data class Service(val item: BBPSDashboard.ServiceItem) : BBPSEntry()
    data class Category(val item: DataCategory) : BBPSEntry()
}
