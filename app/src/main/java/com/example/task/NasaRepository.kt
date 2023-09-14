package com.example.task

import retrofit2.Response

class NasaRepository {
    private val nasaApiService = RetrofitClient.nasaApiService

    suspend fun getNasaImageOfTheDay(apiKey: String): Response<NasaImageData> {
        return nasaApiService.getNasaImageOfTheDay(apiKey)
    }
}
