package com.ahmedabdelmeged.simplelocationtracker.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.ahmedabdelmeged.simplelocationtracker.util.SingletonHolder

/**
 * Room database that will store the photos, We get from the flickr search api at a particular location
 * so when the user open the app anytime during the walk he will see the photos stream even if he/che
 * closed the app and let the tracking background service open in the background.
 */
@Database(entities = [Photo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun pictureDao(): PhotoDao

    /**
     * Initialize [AppDatabase] lazily as singleton to use on demand. For a production
     * app we might use dependency injection to inject the database to where we want to use it.
     */
    companion object : SingletonHolder<AppDatabase, Context>({
        Room.databaseBuilder(it.applicationContext,
                AppDatabase::class.java, "komoot.db")
                .build()
    })

}