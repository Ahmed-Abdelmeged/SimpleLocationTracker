package com.ahmedabdelmeged.simplelocationtracker.data.api

/**
 * The response from flickr sizes api. It return many parameters but we only care about the
 * sizes parameter to extract the photos sizes and download urls.
 */
inline class FlickrPhotoSizeResponse(val sizes: FlickrPhotoSizes?)