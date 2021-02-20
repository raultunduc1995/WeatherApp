package com.example.weatherapp.repository.geocoder.api.domain

import com.google.gson.annotations.SerializedName

data class Component(
    val city: String?,
    val country: String?,
    val county: String?,
    val state: String?,
    val town: String?
)

data class Geometry(
    val lat: Double,
    val lng: Double
)

data class Result(
    @SerializedName("components")
    val component: Component,
    val geometry: Geometry
)

data class LocationData(
    val results: List<Result>
)
