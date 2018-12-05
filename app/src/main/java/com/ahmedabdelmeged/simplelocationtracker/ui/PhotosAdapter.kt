package com.ahmedabdelmeged.simplelocationtracker.ui

import androidx.recyclerview.widget.ListAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.ahmedabdelmeged.simplelocationtracker.R
import com.ahmedabdelmeged.simplelocationtracker.data.db.Photo
import com.ahmedabdelmeged.simplelocationtracker.util.GlideApp
import com.bumptech.glide.load.engine.DiskCacheStrategy

/**
 * Recycler view adapter to display list of [Photo] that coming from the db in [MainActivity].
 */
class PhotosAdapter : ListAdapter<Photo, PhotosAdapter.PhotoViewHolder>(Photo.DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val photoImageView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_photo, parent, false) as ImageView
        return PhotoViewHolder(photoImageView)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bindTo(getItem(position).url)
    }

    inner class PhotoViewHolder(private val view: ImageView) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        fun bindTo(url: String) {
            GlideApp.with(view)
                    .load(url)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(view)
        }
    }

}