package com.example.task

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class CachingManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_cache", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveImageData(imageData: NasaImageData) {
        val json = gson.toJson(imageData)
        sharedPreferences.edit().putString("nasa_image_data", json).apply()
    }

    fun getCachedImageData(): NasaImageData? {
        val json = sharedPreferences.getString("nasa_image_data", null)
        val type: Type = object : TypeToken<NasaImageData>() {}.type
        return gson.fromJson(json, type)
    }
}
