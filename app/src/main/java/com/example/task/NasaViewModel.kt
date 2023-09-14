package com.example.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class NasaViewModel(private val cachingManager: CachingManager) : ViewModel() {
    private val nasaRepository = NasaRepository()

    private val _nasaData = MutableLiveData<Result<NasaImageData>>()
    val nasaData: LiveData<Result<NasaImageData>> get() = _nasaData

    fun getNasaImageOfTheDay(apiKey: String) {
        viewModelScope.launch {
            try {
                val cachedImageData = cachingManager.getCachedImageData()
                if (cachedImageData != null) {
                    // Load cached data into LiveData
                    _nasaData.value = Result.Success(cachedImageData)
                }

                val response = nasaRepository.getNasaImageOfTheDay(apiKey)
                if (response.isSuccessful) {
                    val nasaImageData = response.body()!!

                    if (nasaImageData.media_type == "image") {
                        // It's an image, save and display it
                        cachingManager.saveImageData(nasaImageData)
                        _nasaData.value = Result.Success(nasaImageData)
                    } else if (nasaImageData.media_type == "video") {
                        // It's a video, pass the video URL to MainActivity
                        _nasaData.value = Result.Success(nasaImageData)
                    } else {
                        _nasaData.value = Result.Error("Unsupported media type")
                    }
                } else {
                    _nasaData.value = Result.Error("Failed to fetch data")
                }
            } catch (e: Exception) {
                _nasaData.value = Result.Error(e.message ?: "An error occurred")
            }
        }
    }
}
