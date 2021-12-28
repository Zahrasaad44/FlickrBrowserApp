package com.example.flickrbrowserapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flickrbrowserapp.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import org.json.JSONObject
import java.lang.Exception
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerAdapter: ImageAdapter

    private lateinit var images: ArrayList<Image>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        images = arrayListOf()

        recyclerAdapter = ImageAdapter(this, images)
        binding.imagesRV.adapter = recyclerAdapter
        binding.imagesRV.layoutManager = LinearLayoutManager(this)

        binding.searchBtn.setOnClickListener { requestAPI() }
    }


    private fun requestAPI() {
        CoroutineScope(Dispatchers.IO).launch {
            val data = async { fetchImages() }.await()
            if (data.isNotEmpty()) {
                showImages(data)
            } else {
                Toast.makeText(this@MainActivity, "No images found", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun fetchImages(): String{
        var response = ""
        val tag = binding.searchET.text
        try {
            response = URL("https://www.flickr.com/services/rest/?method=flickr.photos.search&api_key=407784392dcf3a6de266c193706b2c2d&tags=$tag&format=json&nojsoncallback=1")
                .readText()
        } catch (exception: Exception) {
            Log.d("fetch", "Couldn't fetch image $exception")
        }
        return response
    }

    private suspend fun showImages(image: String) {
        withContext(Dispatchers.Main) {
            val jsonObject = JSONObject(image)
            val flickrImages = jsonObject.getJSONObject("photos").getJSONArray("photo")
            Log.d("rr", "errorrrr $flickrImages")
            for(i in 0 until flickrImages.length()) {
                val title = flickrImages.getJSONObject(i).getString("title")
                val serverID = flickrImages.getJSONObject(i).getString("server")
                val id = flickrImages.getJSONObject(i).getString("id")
                val secret = flickrImages.getJSONObject(i).getString("secret")
                val imageLink = "https://live.staticflickr.com/$serverID/${id}_${secret}.jpg"
                images.add(Image(imageLink, title))
            }
            recyclerAdapter.notifyDataSetChanged()
        }
    }
}