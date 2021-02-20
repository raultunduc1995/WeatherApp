package com.example.weatherapp.repository.database.domain

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weatherapp.repository.darksky.api.domain.WeatherInfo

@Entity
data class LocalWeatherInfo(
    @PrimaryKey val city: String,
    @ColumnInfo(name = "country") val country: String,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double,
    @Embedded val weather: WeatherInfo
) {
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is LocalWeatherInfo) {
            return false
        }

        return city == other.city &&
                country == other.country &&
                latitude == other.latitude &&
                longitude == other.longitude &&
                weather == other.weather
    }

    override fun hashCode(): Int {
        var result = city.hashCode()
        result = 31 * result + country.hashCode()
        return result
    }
}
