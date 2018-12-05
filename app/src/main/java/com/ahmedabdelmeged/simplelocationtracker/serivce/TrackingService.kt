package com.ahmedabdelmeged.simplelocationtracker.serivce

import android.content.Context
import android.os.IBinder
import android.content.Intent
import android.app.*
import android.graphics.Color
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.ahmedabdelmeged.simplelocationtracker.extesnions.checkLocationPermission
import android.app.PendingIntent
import com.ahmedabdelmeged.simplelocationtracker.ui.MainActivity
import android.app.NotificationManager
import com.ahmedabdelmeged.simplelocationtracker.R
import com.ahmedabdelmeged.simplelocationtracker.data.TrackingRepository
import com.ahmedabdelmeged.simplelocationtracker.extesnions.distanceBetween
import com.google.android.gms.location.*
import timber.log.Timber

/**
 * Service to track the location updates in the background.
 */
class TrackingService : Service() {

    private val repo = TrackingRepository(this)

    private var client: FusedLocationProviderClient? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        buildNotification()
        requestLocationUpdates()
    }

    private fun buildNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        //Put extra to the activity to know if the serivce is running or not.
        notificationIntent.putExtra(EXTRA_SERVICE_WORKING, true)
        val pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this, getNotificationChannelId())
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.tracking_walk_working))
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher_round)
        startForeground(NOTIFICATION_ID, builder.build())
    }

    /**
     * Create a notification channel when the device API level is above O.
     */
    private fun getNotificationChannelId(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(channelId = getString(R.string.tracking_notification_channel_id),
                    channelName = getString(R.string.tracking_notification_channel_name))
        } else {
            // If earlier version channel ID is not used
            ""
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val channel = NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_DEFAULT)
        channel.lightColor = Color.BLUE
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
        return channelId
    }

    /**
     * Initiate the request to track the device's location
     */
    private fun requestLocationUpdates() {
        client = LocationServices.getFusedLocationProviderClient(this)
        //If the app currently has access to the location permission.
        if (checkLocationPermission()) {
            client?.requestLocationUpdates(getLocationRequest(), locationListener, null)
        }
    }

    private val locationListener = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            val location = locationResult?.lastLocation
            if (location != null) {
                checkNewLocationDistance(location)
            }
        }
    }

    //Temp variable to store last value of the location to calculate the distance.
    private var tempLocation: Location? = null

    /**
     * Check if the distance between the current location and previous location is equal or greater
     * than [DISTANCE_TO_SEARCH_PLACE_PHOTO] and accuracy lower than [MINIMUM_BLOCK_SIZE] to get the
     * place photo from flickr and save it to the db to appear in the photos stream in the UI.
     */
    fun checkNewLocationDistance(location: Location) {
        Timber.d("Got a now location: $location")
        if (tempLocation == null) {
            tempLocation = location
        } else {
            if (location.accuracy <= MIN_ACCURACY && tempLocation!!.distanceBetween(location) >= DISTANCE_TO_SEARCH_PLACE_PHOTO) {
                Timber.d("The user walked $DISTANCE_TO_SEARCH_PLACE_PHOTO, Search for images and save it.")
                tempLocation = location
                repo.getAndSaveImageFromFlicker(lat = location.latitude, lng = location.longitude)
            }
        }
    }

    private fun getLocationRequest(): LocationRequest {
        val request = LocationRequest()

        //Specify how often the app should request the location
        request.interval = REQUEST_LOCATION_INTERVAL

        //Get the most accurate location data available.
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        return request
    }

    override fun onDestroy() {
        super.onDestroy()
        //Make sure to clean the location listener when the service is finished.
        client?.removeLocationUpdates(locationListener)

        //Clear the walk photos and data.
        repo.clear()
    }

    companion object {
        //10 seconds interval to request location update.
        private const val REQUEST_LOCATION_INTERVAL = 10 * 1000L

        //Minimum location accuracy in meters. If the location accuracy greater than this value
        //Mean the location predication will have a bigger error.
        private const val MIN_ACCURACY = 25

        //The distance in meters that after it we will request place photo from flickr.
        private const val DISTANCE_TO_SEARCH_PLACE_PHOTO = 100

        private const val NOTIFICATION_ID = 838
        const val EXTRA_SERVICE_WORKING = "extra_service_working"
    }

}