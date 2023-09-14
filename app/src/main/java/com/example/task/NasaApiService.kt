package com.example.task

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaApiService {
    @GET("planetary/apod")
    suspend fun getNasaImageOfTheDay(@Query("api_key") apiKey: String): Response<NasaImageData>
}
