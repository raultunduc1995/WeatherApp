package com.example.weatherapp.repository.geocoder.api

import com.example.weatherapp.repository.geocoder.api.domain.LocationData
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocoderApiInterface {
    @GET("geocode/v1/json")
    fun getLocation(@Query("q") query: String): Flowable<LocationData>
}
