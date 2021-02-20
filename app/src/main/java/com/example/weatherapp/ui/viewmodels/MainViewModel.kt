package com.example.weatherapp.ui.viewmodels

import com.example.weatherapp.repository.WeatherRepository
import com.example.weatherapp.repository.darksky.api.domain.LocationWeatherDetails
import com.example.weatherapp.repository.geocoder.api.domain.Result
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainViewModel @Inject constructor(
    private val repository: WeatherRepository
) {
    var locationDetails: Result? = null
    var isDataLoaded: Boolean = false
        private set

    fun init() {
        isDataLoaded = false
    }

    fun getLocationDetails(city: String, country: String): Flowable<Result> =
        repository.fetchLocationCoordinates(city, country)
            .map {
                locationDetails = it
                it
            }

    fun getAllWeatherDetails() =
        repository.getAllSavedWeatherData()

    fun getAndSaveSelectedLocationWeather(): Flowable<LocationWeatherDetails> {
        isDataLoaded = true
        return repository.getAndSaveLocationWeather(locationDetails!!)
    }
}
