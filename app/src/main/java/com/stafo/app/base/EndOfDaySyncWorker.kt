package com.stafo.app.base

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.stafo.app.base.network.RetrofitInstance
import com.stafo.app.database.AppDatabase
import com.stafo.app.database.dataClass.LocationEntity
import com.stafo.app.screens.settings.dataClass.EmployeePostLocationRequest
import com.stafo.app.utils.getBatteryPercentage
import com.stafo.app.utils.getEmployeeDetails
import com.stafo.app.utils.getUserAccessToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EndOfDaySyncWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private val locationDao = AppDatabase.getDatabase(appContext).locationDao()

    override suspend fun doWork(): Result {
        val allLocations = locationDao.getAllLocations()
        if (allLocations.isNotEmpty()) {
            val success = postAllLocations(allLocations)
            if (success) {

                locationDao.clearAllLocations()
                return Result.success()
            }
            return Result.retry()
        }
        return Result.success()
    }

    private suspend fun postAllLocations(locations: List<LocationEntity>): Boolean {
        return try {

            val token = getUserAccessToken() ?: return false

            val request = EmployeePostLocationRequest(
                employee_id = getEmployeeDetails()?.id.toString(),
                latitude = locations[0].latitude,
                longitude = locations[0].longitude,
                battery_status = "${getBatteryPercentage(applicationContext)}%"
            )

            CoroutineScope(Dispatchers.IO).launch {

                locations.forEachIndexed { index, location ->
                    Log.d("workM", "Location exact time #$index: $location")
                }
            }


            //val response = RetrofitInstance.apiService.callPostGeoLocation("Bearer $token", request)

           // response.isSuccessful


            true
        } catch (e: Exception) {
            false
        }
    }




}
