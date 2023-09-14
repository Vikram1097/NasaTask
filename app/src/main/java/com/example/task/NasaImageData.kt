package com.example.task

data class NasaImageData(
    val date: String,
    val explanation: String,
    val hdurl: String,
    val media_type: String,
    val title: String,
    val url: String
)

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}


