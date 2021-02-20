package com.example.weatherapp.repository.darksky.api

import com.example.weatherapp.repository.darksky.api.domain.LocationWeatherDetails
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Path

interface DarkSkyApiInterface {
    @GET("forecast/{lat},{lng}")
    fun getWeatherDetails(
        @Path("lat") latitude: Double,
        @Path("lng") longitude: Double
    ): Flowable<LocationWeatherDetails>
}
