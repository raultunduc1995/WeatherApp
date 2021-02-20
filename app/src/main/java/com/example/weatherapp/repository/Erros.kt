package com.example.weatherapp.repository

data class UnknownLocation(override val message: String?) : IllegalStateException()
