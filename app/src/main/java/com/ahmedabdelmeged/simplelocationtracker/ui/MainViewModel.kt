package com.ahmedabdelmeged.simplelocationtracker.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ahmedabdelmeged.simplelocationtracker.data.TrackingRepository
import com.ahmedabdelmeged.simplelocationtracker.data.db.Photo

/**
 * ViewModel for [MainActivity]
 */
class MainViewModel(repository: TrackingRepository) : ViewModel() {

    /**
     * The current photos stored in db.
     */
    val photos: LiveData<List<Photo>> = repository.getAllPhotos()

    /**
     * Factory class to create [MainViewModel] to inject the [TrackingRepository] to it's constructor
     * for better testing.
     */
    class Factory(private val repository: TrackingRepository) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainViewModel(repository) as T
        }
    }

}