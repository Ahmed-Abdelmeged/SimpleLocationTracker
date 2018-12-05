package com.ahmedabdelmeged.simplelocationtracker.data.api

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

/**
 * Retrofit service to interact with Flickr Api for location image search.
 */
interface FlickrService {

    /**
     * Search for a photo by location using flickr search api.
     */
    @GET("?method=flickr.photos.search&api_key=$FLICKR_API_KEY&format=json&nojsoncallback=1")
    fun searchForPhotoByLocation(
            @Query("lat") latitude: Double,
            @Query("lon") longitude: Double
    ): Call<FlickrSearchResponse>

    /**
     * Get photo download link from a flickr photo Id.
     */
    @GET("?method=flickr.photos.getSizes&api_key=$FLICKR_API_KEY&format=json&nojsoncallback=1")
    fun getPhotoUrl(
            @Query("photo_id") photoId: String
    ): Call<FlickrPhotoSizeResponse>

    companion object {
        /**
         * In a production application we won't add the API key like this because it's easy to
         * access if someone reverse engineering the apk so better storing it in a C file using NDK.
         * It will be much harder for hackers to access it. I just put here now for simplicity.
         */
        private const val FLICKR_API_KEY = "YOUR_API_KEY"

        /**
         * Initialize [FlickrService] lazily as singleton to use on demand. For a production
         * app we might use dependency injection to inject the service to where we want to use it.
         */
        val instance: FlickrService by lazy {
            //Logging Interceptor to log Retrofit traffic for debugging.
            val logging = HttpLoggingInterceptor { message -> Timber.d(message) }
            logging.level = HttpLoggingInterceptor.Level.BASIC
            val client = OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build()

            val retrofit = Retrofit.Builder()
                    .client(client)
                    .baseUrl("https://api.flickr.com/services/rest/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            retrofit.create(FlickrService::class.java)
        }
    }

}