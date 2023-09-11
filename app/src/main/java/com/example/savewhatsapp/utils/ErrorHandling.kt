package com.example.statusapplication.utils

sealed class ErrorHandling<out T> {
    data class Success<out T>(val data: T) : ErrorHandling<T>()
    data class Error(val message: String) : ErrorHandling<Nothing>()
    object Loading : ErrorHandling<Nothing>()
}