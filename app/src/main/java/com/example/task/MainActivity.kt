package com.example.task

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.task.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: NasaViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var imageView: ImageView
    private lateinit var webView: WebView
    private lateinit var playButton: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //  app will first check the cache for the NASA "Image of the Day" data before making a network request. This will help reduce unnecessary network requests and provide a faster and more efficient user experience.

        val cachingManager = CachingManager(this)
        viewModel = ViewModelProvider(
            this,
            NasaViewModelFactory(cachingManager)
        ).get(NasaViewModel::class.java)


        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        imageView = findViewById<ImageView>(R.id.imageView)
        val titleTextView = findViewById<TextView>(R.id.titleTextView)
        val dateTextView = findViewById<TextView>(R.id.dateTextView)
        val descriptionTextView = findViewById<TextView>(R.id.descriptionTextView)
        webView = findViewById(R.id.webView)
         playButton = findViewById<ImageButton>(R.id.playButton)


        val refreshButton = findViewById<Button>(R.id.refreshButton)
        refreshButton.setOnClickListener {

            refreshData()
        }

        refreshData()



        viewModel.nasaData.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    val nasaImageData = result.data

                    // Check if it's a video
                    if (nasaImageData.media_type == "video") {
                        // Show the play button
                        val playButton = findViewById<ImageButton>(R.id.playButton)
                        playButton.visibility = View.VISIBLE
                        playButton.setOnClickListener {
                            showVideoContent(nasaImageData.url)
                            // Handle video playback here
                            // You can open the video in a video player or a WebView
                        }
                    } else {
                        // It's an image
                        showImageContent(nasaImageData)
                    }

                    titleTextView.text = nasaImageData.title
                    dateTextView.text = nasaImageData.date
                    descriptionTextView.text = nasaImageData.explanation
                    progressBar.visibility = View.GONE
                }

                is Result.Error -> {
                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                }
            }
        }


        // Replace "YOUR_NASA_API_KEY" with your actual NASA API key
        viewModel.getNasaImageOfTheDay("zMxGmLaT2EsyhXyJi3nihHcM4NrZn1j8bGO0mWcr")
        progressBar.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        // Clear the WebView cache when resuming the activity
        webView.clearCache(true)
    }



    private fun refreshData() {
        progressBar.visibility = View.VISIBLE
        viewModel.getNasaImageOfTheDay("zMxGmLaT2EsyhXyJi3nihHcM4NrZn1j8bGO0mWcr")
    }


    private fun showImageContent(imageData: NasaImageData) {
        // Hide the WebView and play button
        webView.visibility = View.GONE
        playButton.visibility = View.GONE

        // Show the image in the ImageView
        Picasso.get().load(imageData.url).into(imageView)

        // Hide progress bar
        progressBar.visibility = View.GONE
    }
    private fun showVideoContent(videoUrl: String) {
        // Hide the ImageView and show the WebView and play button
        imageView.visibility = View.GONE
        webView.visibility = View.VISIBLE
        playButton.visibility = View.VISIBLE

        // Configure WebView settings
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.mediaPlaybackRequiresUserGesture = false

        // Set WebView client and chrome client
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        // Load the video URL in the WebView
        webView.loadUrl(videoUrl)

        // Handle play button click to open an external video player
        playButton.setOnClickListener {
            openVideoInExternalPlayer(videoUrl)
        }

        // Hide progress bar
        progressBar.visibility = View.GONE
    }

    private fun openVideoInExternalPlayer(videoUrl: String) {
        val videoIntent = Intent(Intent.ACTION_VIEW)
        videoIntent.setDataAndType(Uri.parse(videoUrl), "video/*")

        try {
            startActivity(videoIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No video player app found", Toast.LENGTH_SHORT).show()
        }
    }


}
