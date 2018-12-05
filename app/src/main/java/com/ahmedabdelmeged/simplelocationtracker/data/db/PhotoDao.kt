package com.ahmedabdelmeged.simplelocationtracker.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Room data access object to do operations on the [Photo.PHOTOS_TABLE]
 */
@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPhoto(photo: Photo)

    /**
     * Wrapped around [LiveData] so when a new photo added to the db it will be observed
     * automatically at the UI to avoid pulling the db and have better lifecycle support.
     */
    @Query("SELECT * FROM ${Photo.PHOTOS_TABLE}")
    fun getAllPhotos(): LiveData<List<Photo>>

    @Query("DELETE from ${Photo.PHOTOS_TABLE}")
    fun deleteAllPhotos()

}