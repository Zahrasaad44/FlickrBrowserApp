package com.example.flickrbrowserapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flickrbrowserapp.databinding.ImageRowBinding

class ImageAdapter(private val activity: MainActivity, private val images: ArrayList<Image>): RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    class ImageViewHolder(val binding: ImageRowBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            ImageRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = images[position]

        holder.binding.apply {
            imageTitle.text = image.title
            Glide.with(activity) //"activity" refers to the activity which will be passed to the Glide library, i.e. MainActivity. It is possible to use "this" in other cases.
                .load(image.link) // Loads the image url into Glide
                .into(thumbnailIV) // determines the holder for the image (ImageView)

            thumbnailIV.setOnClickListener { activity.displayClickedImage(image) }
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }
}