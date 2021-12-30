package com.example.flickrbrowserapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.flickrbrowserapp.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import org.json.JSONObject
import java.lang.Exception
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerAdapter: ImageAdapter

    private lateinit var images: ArrayList<Image>
    private val apiKey = "407784392dcf3a6de266c193706b2c2d"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        images = arrayListOf()

        recyclerAdapter = ImageAdapter(this, images)
        binding.imagesRV.adapter = recyclerAdapter
        binding.imagesRV.layoutManager = GridLayoutManager(this, 2)

        binding.searchBtn.setOnClickListener {
            if (binding.searchET.text.isNotEmpty()) {
                requestAPI()
            } else {
                Toast.makeText(this, "Please type something to search", Toast.LENGTH_LONG).show()
            }
        }
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
            response = URL("https://www.flickr.com/services/rest/?method=flickr.photos.search&api_key=$apiKey&tags=$tag&format=json&nojsoncallback=1")
                .readText()   // The "api_key" that is generated with the URL is working too
        } catch (exception: Exception) {
            Log.d("fetch", "Couldn't fetch image $exception")
        }
        return response
    }

    private suspend fun showImages(image: String) {
        withContext(Dispatchers.Main) {
            val jsonObject = JSONObject(image)
            val flickrImages = jsonObject.getJSONObject("photos").getJSONArray("photo")
            for(i in 0 until flickrImages.length()) {
                val title = flickrImages.getJSONObject(i).getString("title")
                val serverID = flickrImages.getJSONObject(i).getString("server")
                val id = flickrImages.getJSONObject(i).getString("id")
                val secret = flickrImages.getJSONObject(i).getString("secret")
                val imageLink = "https://live.staticflickr.com/$serverID/${id}_${secret}.jpg"
                // the structure of imageLink: https://www.flickr.com/services/api/misc.urls.html
                images.add(Image(imageLink, title))
            }
            recyclerAdapter.notifyDataSetChanged()
        }
    }


    fun displayClickedImage(anImage: Image) {
        val intent = Intent(this, DetailedImage::class.java)
        intent.putExtra("image", anImage.link)
        intent.putExtra("title", anImage.title)
        startActivity(intent)
    }
}