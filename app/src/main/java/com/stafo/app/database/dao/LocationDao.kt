package com.stafo.app.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stafo.app.database.dataClass.LocationEntity

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLocation(location: LocationEntity)

    @Query("SELECT * FROM location_table WHERE isNetwork = 0")
    suspend fun getUnsyncedLocations(): List<LocationEntity>

    @Query("UPDATE location_table SET isNetwork = 1 WHERE id IN (:ids)")
    suspend fun markLocationsAsSynced(ids: List<Int>)

    @Query("SELECT * FROM location_table")
    suspend fun getAllLocations(): List<LocationEntity>

    @Query("DELETE FROM location_table")
    suspend fun clearAllLocations()
}