package com.ahmedabdelmeged.simplelocationtracker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.recyclerview.widget.DiffUtil

/**
 * The photo entity that will be stored in the db.
 */
@Entity(tableName = Photo.PHOTOS_TABLE)
data class Photo(
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0,
        val latitude: Double,
        val longitude: Double,
        val url: String) {

    companion object {
        //Photos SQL table name.
        const val PHOTOS_TABLE = "photos_table"

        /**
         * Diff callback used in [com.ahmedabdelmeged.simplelocationtracker.ui.PhotosAdapter] to calculate
         * the difference between photos in a background thread before adding it to the adapter to
         * have better animation and performance.
         */
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Photo>() {
            override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
                //We use the url to identify if the photos are the same or not to avoid adding the same
                //photo to the adapter.
                return newItem.url == newItem.url
            }

            override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
                return newItem == oldItem
            }
        }
    }

}