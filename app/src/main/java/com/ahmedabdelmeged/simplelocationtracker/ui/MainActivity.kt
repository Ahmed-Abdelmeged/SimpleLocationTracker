package com.ahmedabdelmeged.simplelocationtracker.ui

import android.Manifest
import androidx.lifecycle.ViewModelProviders
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Context
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.location.LocationManager
import android.content.Intent
import android.view.View
import com.ahmedabdelmeged.simplelocationtracker.R
import com.ahmedabdelmeged.simplelocationtracker.data.TrackingRepository
import com.ahmedabdelmeged.simplelocationtracker.serivce.TrackingService
import com.ahmedabdelmeged.simplelocationtracker.extesnions.checkLocationPermission
import com.ahmedabdelmeged.simplelocationtracker.extesnions.nonNullObserve
import com.ahmedabdelmeged.simplelocationtracker.extesnions.toast
import timber.log.Timber

/**
 * Activity to display list of walk photos from the db.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    private lateinit var adapter: PhotosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Setup photos adapter.
        adapter = PhotosAdapter()
        picturesRecyclerView.adapter = adapter

        //Observe the photos from the db.
        val viewModelFactory = MainViewModel.Factory(TrackingRepository(applicationContext))
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
        viewModel.photos.nonNullObserve(this) {
            Timber.d("Photos list: $it")
            adapter.submitList(it)
        }

        //Check if we launch the activity from the service notification.
        val isServiceWorking = intent.getBooleanExtra(TrackingService.EXTRA_SERVICE_WORKING, false)
        if (isServiceWorking) {
            trackingWorkingUiState()
        }

        startButton.setOnClickListener {
            //Check if the GPS tracking is enabled
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                toast(R.string.location_service_status_un_available)
            } else {
                startTrackingService()
            }
        }

        stopButton.setOnClickListener { stopTrackingService() }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSIONS_REQUEST && grantResults.size == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startTrackingService()
        } else {
            toast(R.string.location_service_status_permission_denied)
        }
    }

    private fun startTrackingService() {
        if (checkLocationPermission()) {
            startService(Intent(this, TrackingService::class.java))
            trackingWorkingUiState()

            //Notify the user that tracking has been enabled.
            toast(R.string.location_service_status_started)
        } else {
            //If the app doesn’t currently have access to the user’s location, then request access.
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSIONS_REQUEST
            )
        }
    }

    private fun trackingWorkingUiState() {
        walkTrackingGroup.visibility = View.VISIBLE
        startButton.visibility = View.GONE
    }

    private fun stopTrackingService() {
        stopService(Intent(this, TrackingService::class.java))
        walkTrackingGroup.visibility = View.GONE
        startButton.visibility = View.VISIBLE
    }

    companion object {
        private const val LOCATION_PERMISSIONS_REQUEST = 546
    }

}