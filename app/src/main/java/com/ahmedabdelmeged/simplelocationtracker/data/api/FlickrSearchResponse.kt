package com.ahmedabdelmeged.simplelocationtracker.data.api

/**
 * The response from flickr search api. It return many parameters but we only care about the
 * photos parameter to extract the photo id that associated with the provided location
 */
inline class FlickrSearchResponse(val photos: FlickrSearchPhoto?)