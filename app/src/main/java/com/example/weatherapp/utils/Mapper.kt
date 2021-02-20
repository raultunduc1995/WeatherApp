package com.example.weatherapp.utils

import com.example.weatherapp.repository.darksky.api.domain.WeatherInfo
import com.example.weatherapp.repository.database.domain.LocalWeatherInfo
import com.example.weatherapp.repository.geocoder.api.domain.Result
import java.util.*

interface Mapper {
    fun mapLocalWeatherDetailsToDBEntity(
        locationDetails: Result,
        weatherInfo: WeatherInfo
    ): LocalWeatherInfo
}

class WeatherDataMapper : Mapper {
    override fun mapLocalWeatherDetailsToDBEntity(
        locationDetails: Result,
        weatherInfo: WeatherInfo
    ): LocalWeatherInfo {
        val component = locationDetails.component
        val city = when {
            component.city != null -> {
                component.city
            }
            component.town != null -> {
                component.town
            }
            component.county != null -> {
                component.county
            }
            component.state != null -> {
                component.state
            }
            else -> ""
        }

        return LocalWeatherInfo(
            city,
            component.country ?: "",
            locationDetails.geometry.lat,
            locationDetails.geometry.lng,
            WeatherInfo(
                weatherInfo.icon,
                weatherInfo.temperature,
                Date().time
            )
        )
    }
}
