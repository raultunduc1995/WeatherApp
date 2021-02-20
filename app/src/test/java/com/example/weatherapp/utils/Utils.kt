package com.example.weatherapp.utils

inline fun <reified T> doSafely(block: () -> T): T? {
    try {
        return block.invoke()
    } catch (exception: Exception) {
        exception.printStackTrace()
    }
    return null
}
