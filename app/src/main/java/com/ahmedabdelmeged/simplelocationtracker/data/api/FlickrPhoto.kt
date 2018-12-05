package com.ahmedabdelmeged.simplelocationtracker.data.api

/**
 * The photo object that returned from the flickr search api. We only care about the photo id to use
 * it to get the download url of it.
 */
inline class FlickrPhoto(val id: String?)