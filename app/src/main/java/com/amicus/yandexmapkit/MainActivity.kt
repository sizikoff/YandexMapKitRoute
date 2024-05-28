package com.amicus.yandexmapkit

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingRouterType
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.Error

class MainActivity : AppCompatActivity(),DrivingSession.DrivingRouteListener {

    lateinit var mapView:MapView
    private lateinit var map: Map
    private lateinit var drivingRouter: DrivingRouter
    private var drivingSession: DrivingSession? = null
    private lateinit var placemarksCollection: MapObjectCollection
    private lateinit var routesCollection: MapObjectCollection
    lateinit var START_POSITION:Point
    var END = Point(56.833742, 60.635716)
    lateinit var fused :FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mapView = findViewById(R.id.mapview )
        var mapkit:MapKit = MapKitFactory.getInstance()
        map = mapView.mapWindow.map
        requestLocation()
        var locationc = mapkit.createUserLocationLayer(mapView.mapWindow)
        locationc.isVisible = true
        Log.d("COORD",locationc.toString())
        Animation(Animation.Type.LINEAR,300f)
        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val lastKnownLocation =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            lastKnownLocation?.let { location ->
                val latitude = location.latitude
                val longitude = location.longitude
                // Используйте координаты

                START_POSITION = Point(lastKnownLocation?.latitude.toString().toDouble(),lastKnownLocation?.longitude.toString().toDouble())

                fused = LocationServices.getFusedLocationProviderClient(this)
                fused.lastLocation.addOnSuccessListener {location: Location? ->
                    END = Point(location?.latitude.toString().toDouble(),location?.longitude.toString().toDouble())
                    true
                }
            }


        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.ONLINE)
        placemarksCollection = map.mapObjects.addCollection()
        routesCollection = map.mapObjects.addCollection()

        submitRequest()


    }

    fun requestLocation(){
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),0)
            return
        }

    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onDrivingRoutes(p0: MutableList<DrivingRoute>) {
        for (route in p0) {
            routesCollection.addPolyline(route.geometry)
        }
        }

        override fun onDrivingRoutesError(p0: Error) {
            var error = "Erro"
        }

    private fun submitRequest() {
        var drivingOptions = DrivingOptions()
        var vehcleOptions = VehicleOptions()
        var requestPoints:ArrayList<RequestPoint> = ArrayList()
        requestPoints.add(RequestPoint(START_POSITION,RequestPointType.WAYPOINT,null,null))
        requestPoints.add(RequestPoint(END,RequestPointType.WAYPOINT,null,null))
        drivingSession = drivingRouter!!.requestRoutes(requestPoints,drivingOptions,vehcleOptions,this)
    }
}