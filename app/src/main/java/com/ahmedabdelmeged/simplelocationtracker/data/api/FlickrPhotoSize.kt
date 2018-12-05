package com.ahmedabdelmeged.simplelocationtracker.data.api

/**
 * The photo size object returned from flickr sizes api. That have the download urls and photo sizes.
 */
data class FlickrPhotoSize(
        val label: String? = null,
        val width: Int = 0,
        val height: Int = 0,
        val source: String? = null,
        val url: String? = null,
        val media: String? = null
)