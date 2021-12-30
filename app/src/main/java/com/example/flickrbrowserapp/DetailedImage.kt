package com.example.flickrbrowserapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.flickrbrowserapp.databinding.ActivityDetailedImageBinding

class DetailedImage : AppCompatActivity() {
    private lateinit var binding: ActivityDetailedImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()


        val imgLink = intent.getStringExtra("image")
        binding.imageTitleTV.text = intent.getStringExtra("title")

        Glide.with(this).load(imgLink).into(binding.biggerIV)
    }
}