package com.stafo.app.screens.ui

import android.app.DatePickerDialog
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mmi.MapView
import com.stafo.app.R
import com.stafo.app.databinding.ActivityAutoSearchPlaceBinding
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.dataClass.GeoLocationHistResquest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.mmi.MapmyIndiaMapView
import com.mmi.layers.Marker
import com.mmi.layers.PathOverlay
import com.mmi.layers.Polygon
import com.mmi.util.GeoPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AutoSearchPlaceActivity : AppCompatActivity() {


    private lateinit var binding:ActivityAutoSearchPlaceBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    private lateinit var mEMPID:String
    private val calendar = Calendar.getInstance()
    private var mSelectedDate = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityAutoSearchPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)
        mEMPID = intent.getStringExtra("EMP_ID") ?: ""
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        mSelectedDate = currentDate


        onClickListener()
        observeViewModel()

    }






    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


    private fun onClickListener() {
        binding?.apply {

            val showCurrentDate = SimpleDateFormat("dd MMM yy", Locale.getDefault()).format(calendar.time)
            binding.txtDate.setText(showCurrentDate)

            val request = GeoLocationHistResquest(
               employee_id =mEMPID,
                date = mSelectedDate
            )
            Log.d("res","emp get :$request")

            settingsViewModel.getGeoLocationHist(this@AutoSearchPlaceActivity, request)


            imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }

            llCalendar.setOnClickListener {
                showDatePicker()
            }


        }
    }




    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }
   /* private fun addMarkersAndPath(geoPoints: List<GeoPoint>) {
        val mapmyIndiaMapView = findViewById<MapmyIndiaMapView>(R.id.idMapView)
        val mapView = mapmyIndiaMapView.mapView

        if (geoPoints.isEmpty()) return

        val geoPointsArrayList = ArrayList(geoPoints)

        // Add Start Marker (First Point)
        val startMarker = Marker(mapView).apply {
            position = geoPointsArrayList.first()
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Start Point"
        }
        mapView.overlays.add(startMarker)

        // Add End Marker (Last Point)
        val endMarker = Marker(mapView).apply {
            position = geoPointsArrayList.last()
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "End Point"
        }
        mapView.overlays.add(endMarker)

        // Draw polyline between points
        val pathOverlay = PathOverlay(this).apply {
            color = ContextCompat.getColor(this@AutoSearchPlaceActivity,android.R.color.holo_red_dark)
            width = 10f
            points = geoPointsArrayList
        }

        mapView.overlays.add(pathOverlay)
        mapView.setBounds(geoPointsArrayList)
        mapView.invalidate()
    }*/


    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }



        settingsViewModel.mGeoLocationHistResponse.observe(this) {

            if (it.status) {

                if (it.data.isNotEmpty()){
                    binding.idMapView.visibility=View.VISIBLE
                    binding.layoutNotView.visibility=View.GONE
                    val geoPoints = it.data.map {
                        GeoPoint(it.latitude.toDouble(), it.longitude.toDouble())
                    }

                    addMarkersAndPath(geoPoints)
                }else{
                    binding.idMapView.visibility=View.GONE
                    binding.layoutNotView.visibility=View.VISIBLE
                }



            } else {
                CustomToast(this,it.message)
            }


        }





    }


    private fun addMarkersAndPath(points: List<GeoPoint>) {
        val mapmyIndiaMapView = findViewById<MapmyIndiaMapView>(R.id.idMapView)
        val mapView = mapmyIndiaMapView.mapView

        if (points.isEmpty()) return
        if (mapView.overlays == null) return

        mapView.overlays.clear()

        val geoPointsArrayList = ArrayList(points)

        val startMarker = Marker(mapView).apply {
            position = geoPointsArrayList.first()
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Start Point"
        }
        mapView.overlays.add(startMarker)

        val endMarker = Marker(mapView).apply {
            position = geoPointsArrayList.last()
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "End Point"
        }
        mapView.overlays.add(endMarker)

        val pathOverlay = PathOverlay(this).apply {
            color = ContextCompat.getColor(this@AutoSearchPlaceActivity, android.R.color.holo_red_dark)
            width = 10f
            this.points = geoPointsArrayList
        }
        mapView.overlays.add(pathOverlay)

        drawPolygonsForStayDuration(mapView, geoPointsArrayList)

        mapView.setBounds(geoPointsArrayList)
        mapView.invalidate()
    }



    /*private fun addMarkersAndPath(points: List<GeoPoint>) {
        val mapmyIndiaMapView = findViewById<MapmyIndiaMapView>(R.id.idMapView)
        val mapView = mapmyIndiaMapView.mapView






        if (points.isEmpty()) return

        val geoPointsArrayList = ArrayList(points)

        // ➤ Add Start Marker
        val startMarker = Marker(mapView).apply {
            position = geoPointsArrayList.first()
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Start Point"
        }
        mapView.overlays.add(startMarker)

        // ➤ Add End Marker
        val endMarker = Marker(mapView).apply {
            position = geoPointsArrayList.last()
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "End Point"
        }
        mapView.overlays.add(endMarker)

        // ➤ Draw Polyline Path
        val pathOverlay = PathOverlay(this).apply {
            color = ContextCompat.getColor(this@AutoSearchPlaceActivity, android.R.color.holo_red_dark)
            width = 10f
            this.points = geoPointsArrayList
        }
        mapView.overlays.add(pathOverlay)

        // ➤ Draw Polygon Around Nearby Points
        drawPolygonsForStayDuration(mapView, geoPointsArrayList)

        mapView.setBounds(geoPointsArrayList)
        mapView.invalidate()
    }*/

    private fun drawPolygonsForStayDuration(mapView: MapView, points: List<GeoPoint>) {
        val radius = 50.0 // meters
        val stayDurationThreshold = 30 * 1000 // 30 seconds in milliseconds
        val clusters = mutableListOf<List<GeoPoint>>()
        var cluster = mutableListOf<GeoPoint>()
        var lastTime = System.currentTimeMillis()

        // Iterate through the points and cluster based on proximity and time spent in a location
        for (i in 0 until points.size) {
            val point = points[i]

            if (i > 0) {
                val prevPoint = points[i - 1]
                val timeDifference = System.currentTimeMillis() - lastTime

                // If within radius and stay duration is longer than threshold, consider it as staying at the same location
                if (isWithinRadius(prevPoint, point, radius) && timeDifference < stayDurationThreshold) {
                    cluster.add(point)
                } else {
                    // If a cluster has more than 2 points, add it to clusters
                    if (cluster.size >= 3) {
                        clusters.add(cluster)
                    }
                    // Start new cluster
                    cluster = mutableListOf(point)
                }
            }

            lastTime = System.currentTimeMillis()
        }

        // Add last cluster if it has sufficient points
        if (cluster.size >= 3) {
            clusters.add(cluster)
        }

        // Add polygons for each cluster
        for (cluster in clusters) {
            val polygon = Polygon(this@AutoSearchPlaceActivity).apply {
                setPoints(cluster)
                fillColor = ContextCompat.getColor(this@AutoSearchPlaceActivity, R.color.semiTransparentBlue)
                strokeColor = ContextCompat.getColor(this@AutoSearchPlaceActivity, android.R.color.holo_blue_dark)
                strokeWidth = 5f
            }
            mapView.overlays.add(polygon)
        }
    }

    private fun isWithinRadius(p1: GeoPoint, p2: GeoPoint, radius: Double): Boolean {
        val result = FloatArray(1)
        Location.distanceBetween(p1.latitude, p1.longitude, p2.latitude, p2.longitude, result)
        return result[0] <= radius
    }



    /*  private fun addMarkersAndPath(points: List<GeoPoint>) {
          val mapmyIndiaMapView = findViewById<MapmyIndiaMapView>(R.id.idMapView)
          val mapView = mapmyIndiaMapView.mapView

          if (points.isEmpty()) return

          // Clear existing overlays before adding new ones
          mapView.overlays.clear()

          val geoPointsArrayList = ArrayList(points)

          // ➤ Add Start Marker
          val startMarker = Marker(mapView).apply {
              position = geoPointsArrayList.first()
              setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
              title = "Start Point"
          }
          mapView.overlays.add(startMarker)

          // ➤ Add End Marker
          val endMarker = Marker(mapView).apply {
              position = geoPointsArrayList.last()
              setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
              title = "End Point"
          }
          mapView.overlays.add(endMarker)

          // ➤ Draw Polyline Path
          val pathOverlay = PathOverlay(this).apply {
              color = ContextCompat.getColor(this@AutoSearchPlaceActivity, android.R.color.holo_red_dark)
              width = 10f
              this.points = geoPointsArrayList
          }
          mapView.overlays.add(pathOverlay)

          // ➤ Draw Polygon Around Nearby Points
          drawPolygonsForNearbyClusters(mapView, geoPointsArrayList)

          mapView.setBounds(geoPointsArrayList)
          mapView.invalidate()
      }

      private fun drawPolygonsForNearbyClusters(mapView: MapView, points: List<GeoPoint>) {
          val radius = 100.0 // meters
          val unclusteredPoints: MutableList<GeoPoint> = points.toMutableList()
          val clusters = mutableListOf<List<GeoPoint>>()

          while (unclusteredPoints.isNotEmpty()) {
              val basePoint = unclusteredPoints.removeAt(0)
              val cluster = mutableListOf(basePoint)

              val iterator = unclusteredPoints.iterator()
              while (iterator.hasNext()) {
                  val candidate = iterator.next()
                  if (isWithinRadius(basePoint, candidate, radius)) {
                      cluster.add(candidate)
                      iterator.remove()
                  }
              }

              // Add the cluster to the list
              clusters.add(cluster)
          }

          // Add polygons for each cluster
          for (cluster in clusters) {
              if (cluster.size >= 5) { // Ensure there are at least 2 points in the cluster to form a polygon
                  val polygon = Polygon(this@AutoSearchPlaceActivity).apply {
                      setPoints(cluster) // Add all points in the cluster
                      fillColor = ContextCompat.getColor(this@AutoSearchPlaceActivity, R.color.semiTransparentBlue)
                      strokeColor = ContextCompat.getColor(this@AutoSearchPlaceActivity, android.R.color.holo_blue_dark)
                      strokeWidth = 5f
                  }
                  mapView.overlays.add(polygon)
              }
          }
      }

      private fun isWithinRadius(p1: GeoPoint, p2: GeoPoint, radius: Double): Boolean {
          val result = FloatArray(1)
          Location.distanceBetween(p1.latitude, p1.longitude, p2.latitude, p2.longitude, result)
          return result[0] <= radius
      }
  */


    /*   private fun addMarkersAndPath(points: List<GeoPoint>) {
          val mapmyIndiaMapView = findViewById<MapmyIndiaMapView>(R.id.idMapView)
          val mapView = mapmyIndiaMapView.mapView

          if (points.isEmpty()) return

          val geoPointsArrayList = ArrayList(points)

          // ➤ Add Start Marker
          val startMarker = Marker(mapView).apply {
              position = geoPointsArrayList.first()
              setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
              title = "Start Point"
          }
          mapView.overlays.add(startMarker)

          // ➤ Add End Marker
          val endMarker = Marker(mapView).apply {
              position = geoPointsArrayList.last()
              setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
              title = "End Point"
          }
          mapView.overlays.add(endMarker)

          // ➤ Draw Polyline Path
          val pathOverlay = PathOverlay(this).apply {
              color = ContextCompat.getColor(this@AutoSearchPlaceActivity, android.R.color.holo_red_dark)
              width = 10f
              this.points = geoPointsArrayList
          }
          mapView.overlays.add(pathOverlay)

          // ➤ Draw Polygon Around Nearby Points
          drawPolygonsForNearbyClusters(mapView, geoPointsArrayList)

          mapView.setBounds(geoPointsArrayList)
          mapView.invalidate()
      }

       private fun drawPolygonsForNearbyClusters(mapView: MapView, points: List<GeoPoint>) {
           val radius = 50.0 // meters
           val unclusteredPoints: MutableList<GeoPoint> = points.toMutableList()
           val clusters = mutableListOf<List<GeoPoint>>()

           while (unclusteredPoints.isNotEmpty()) {
               val basePoint = unclusteredPoints.removeAt(0)
               val cluster = mutableListOf(basePoint)

               val iterator = unclusteredPoints.iterator()
               while (iterator.hasNext()) {
                   val candidate = iterator.next()
                   if (isWithinRadius(basePoint, candidate, radius)) {
                       cluster.add(candidate)
                       iterator.remove()
                   }
               }

               if (cluster.size >= 3) {
                   clusters.add(cluster)
               }
           }

           for (cluster in clusters) {
               val polygon = Polygon(this@AutoSearchPlaceActivity)
               polygon.setPoints(cluster)
               polygon.fillColor = ContextCompat.getColor(this@AutoSearchPlaceActivity, R.color.semiTransparentBlue)
               polygon.strokeColor = ContextCompat.getColor(this@AutoSearchPlaceActivity, android.R.color.holo_blue_dark)
               polygon.strokeWidth = 5f

               mapView.overlays.add(polygon)
           }
       }




       private fun isWithinRadius(p1: GeoPoint, p2: GeoPoint, radius: Double): Boolean {
           val result = FloatArray(1)
           Location.distanceBetween(p1.latitude, p1.longitude, p2.latitude, p2.longitude, result)
           return result[0] <= radius
       }*/






    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this, { _, year, monthOfYear,dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)

                val dateFormat = SimpleDateFormat("dd MMM yy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                binding.txtDate.setText(formattedDate)

                mSelectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)

                val request = GeoLocationHistResquest(
                    employee_id =mEMPID,
                    date = mSelectedDate
                )
                Log.d("res","emp get :$request")

                settingsViewModel.getGeoLocationHist(this@AutoSearchPlaceActivity, request)



            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        try {
            val datePicker = datePickerDialog.datePicker
            val daySpinner = datePicker.findViewById<View>(
                resources.getIdentifier("day", "id", "android")
            )
            daySpinner?.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }

        datePickerDialog.show()
    }




}
