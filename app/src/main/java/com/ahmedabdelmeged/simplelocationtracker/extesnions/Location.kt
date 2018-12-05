package com.ahmedabdelmeged.simplelocationtracker.extesnions

import android.location.Location

/**
 * Calculate the distance between two [Location] objects.
 */
fun Location.distanceBetween(location: Location): Double {
    return distance(
            lat1 = this.latitude,
            lat2 = location.latitude,

            lng1 = this.longitude,
            lng2 = location.longitude,

            el1 = this.altitude,
            el2 = location.altitude
    )
}

/**
 * Calculate distance between two points in latitude and longitude taking
 * into account height difference. If you are not interested in height
 * difference pass 0.0. Uses Haversine method as its base.
 *
 * lat1, lng1 Start point lat2, lng2 End point el1 Start altitude in meters
 * el2 End altitude in meters
 * @returns Distance in Meters
 */
private fun distance(lat1: Double, lat2: Double, lng1: Double,
                     lng2: Double, el1: Double, el2: Double): Double {
    val r = 6371 // Radius of the earth

    val latDistance = Math.toRadians(lat2 - lat1)
    val lonDistance = Math.toRadians(lng2 - lng1)
    val a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + (Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2))
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    var distance = r.toDouble() * c * 1000.0 // convert to meters

    val height = el1 - el2

    distance = Math.pow(distance, 2.0) + Math.pow(height, 2.0)

    return Math.sqrt(distance)
}