package com.ahmedabdelmeged.simplelocationtracker.data

import androidx.lifecycle.LiveData
import android.content.Context
import android.content.res.Resources
import com.ahmedabdelmeged.simplelocationtracker.data.api.FlickrPhotoSizeResponse
import com.ahmedabdelmeged.simplelocationtracker.data.api.FlickrSearchResponse
import com.ahmedabdelmeged.simplelocationtracker.data.api.FlickrService
import com.ahmedabdelmeged.simplelocationtracker.data.db.AppDatabase
import com.ahmedabdelmeged.simplelocationtracker.data.db.Photo
import com.ahmedabdelmeged.simplelocationtracker.util.GlideApp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.concurrent.Executors
import android.util.DisplayMetrics

/**
 * Repository to interact with [AppDatabase] and [FlickrService] to provide a clean
 * methods to the rest of the app and hide the IO complexity. In a production app we should
 * inject both objects to the constructor so we can mock them for better testing.
 *
 * @param context we need a context to initialize [AppDatabase]
 */
class TrackingRepository(private val context: Context) {

    /**
     * Executor to do database operations and background thread to avoid locking the main thread.
     */
    private val executor = Executors.newSingleThreadExecutor()

    /**
     * Get the photos for the current walk from the database.
     */
    fun getAllPhotos(): LiveData<List<Photo>> {
        return AppDatabase.getInstance(context).pictureDao().getAllPhotos()
    }

    /**
     * When the user run for 100 meters we will request the image of his location from flickr search api
     * and added to the db so he/she can saw it in his photos stream in the UI. We do three
     * operations per request. If any of the operations failed. It will fail silently without any UI changes.
     * We will just log it to our logging service for analytics and investigations.
     *
     * 1- Call search api with the location to get photos for that location.
     * 2- Take the first photo from the list of photos and call the sizes api to get downloadable urls.
     * 3- Save the photo url and location in the db to be shown in the UI.
     */
    fun getAndSaveImageFromFlicker(lat: Double, lng: Double) {
        FlickrService.instance.searchForPhotoByLocation(lat, lng).enqueue(object : Callback<FlickrSearchResponse> {
            override fun onResponse(call: Call<FlickrSearchResponse>, response: Response<FlickrSearchResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    Timber.d("Search for a photo success: $body")
                    body?.photos?.photo?.getOrNull(0)?.apply {
                        if (id != null) {
                            //We get a photo for that location successfully.
                            //Try to get the url for that photo to save it.
                            getPhotoSizes(photoId = id) {
                                savePhoto(lat, lng, it)
                            }
                        }
                    }
                } else {
                    Timber.w("Failed to search for a photo")
                }
            }

            override fun onFailure(call: Call<FlickrSearchResponse>, t: Throwable) {
                Timber.w("Failed to get result from filckr search api: $t")
            }
        })
    }

    /**
     * Get the photo sizes to get a downloadable urls so we can save it in the db and load it latter
     */
    private fun getPhotoSizes(photoId: String, urlCallback: (String) -> Unit) {
        FlickrService.instance.getPhotoUrl(photoId).enqueue(object : Callback<FlickrPhotoSizeResponse> {
            override fun onResponse(
                    call: Call<FlickrPhotoSizeResponse>,
                    response: Response<FlickrPhotoSizeResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    Timber.d("Get photo info success: $body")
                    body?.sizes?.size?.let {
                        //First we will check the device width to load a proper photo size.
                        //If we don't find the device one's. We will try to load image with height
                        //200. Else get the first image or null if there is no photos from the response.
                        val mediumPhoto = it.find { size -> size.label == getSizeLabel() }
                                ?: it.find { size -> size.height >= 200 }
                                ?: it.firstOrNull()
                        mediumPhoto?.source?.let { url -> urlCallback(url) }
                    }
                } else {
                    Timber.w("Failed to get info for photo")
                }
            }

            override fun onFailure(call: Call<FlickrPhotoSizeResponse>, t: Throwable) {
                Timber.w("Failed to get photo info from filckr info api: $t")
            }
        })
    }

    /**
     * Save the photo in the database with it's location information.
     */
    private fun savePhoto(lat: Double, lng: Double, url: String) {
        executor.execute {
            try {
                AppDatabase.getInstance(context).pictureDao().addPhoto(Photo(
                        latitude = lat,
                        longitude = lng,
                        url = url))
                Timber.d("Imaged saved successfully to the db.")
            } catch (e: Exception) {
                Timber.w("Failed to save photo to the db: $e")
            }
        }
    }

    /**
     * When the user finishing his walk we need to clear the current walk. By removing the current
     * photos from the db and clear [GlideApp] cash.
     */
    fun clear() {
        executor.execute {
            try {
                AppDatabase.getInstance(context).pictureDao().deleteAllPhotos()
                GlideApp.get(context).clearDiskCache()
                Timber.d("The walk information cleared successfully")
            } catch (e: Exception) {
                Timber.w("Failed to clear the past walk info: $e")
            }
        }
    }

    /**
     * Get the proper size label that will be used in the recycler view.
     * We can improve that by linking evey photo size to a device width. Currently we will
     * only checking for the tablet case.
     */
    private fun getSizeLabel(): String {
        return if (isTablet()) {
            "Medium 800"
        } else {
            "Medium 640"
        }
    }

    /**
     * Check if the device is tablet or not based on the sceen width.
     */
    private fun isTablet(): Boolean {
        return getScreenWidth() >= convertDpToPixel(600f, context)
    }

    /**
     * Get the current device screen width.
     */
    private fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    private fun convertDpToPixel(dp: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

}