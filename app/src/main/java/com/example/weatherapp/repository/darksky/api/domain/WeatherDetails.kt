package com.example.weatherapp.repository.darksky.api.domain

import android.annotation.SuppressLint
import androidx.room.ColumnInfo
import com.example.weatherapp.R
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

const val DATE_PATTERN = "HH:mm dd-MM-yyyy"

object WeatherIcons {
    const val CLEAR_DAY = "clear-day"
    const val CLEAR_NIGHT = "clear-night"
    const val RAIN = "rain"
    const val SNOW = "snow"
    const val SLEET = "sleet"
    const val WIND = "wind"
    const val FOG = "fog"
    const val CLOUDY = "cloudy"
    const val PARTLY_CLOUDY_DAY = "partly-cloudy-day"
    const val PARTLY_CLOUDY_NIGHT = "partly-cloudy-night"
}

data class WeatherInfo(
    @ColumnInfo(name = "icon") val icon: String,
    @ColumnInfo(name = "temperature") val temperature: Double,
    @ColumnInfo(name = "timestamp") val time: Long
) {
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is WeatherInfo) {
            return false
        }

        return icon == other.icon &&
                temperature == other.temperature &&
                time == other.time
    }

    override fun hashCode(): Int {
        var result = icon.hashCode()
        result = 31 * result + temperature.hashCode()
        result = 31 * result + time.hashCode()
        return result
    }

    fun getIconResourceId(): Int {
        return when (icon) {
            WeatherIcons.CLEAR_DAY -> R.drawable.ic_clear_day
            WeatherIcons.CLEAR_NIGHT -> R.drawable.ic_clear_night
            WeatherIcons.RAIN -> R.drawable.ic_rain
            WeatherIcons.SNOW -> R.drawable.ic_snow
            WeatherIcons.SLEET -> R.drawable.ic_sleet
            WeatherIcons.WIND -> R.drawable.ic_wind
            WeatherIcons.FOG -> R.drawable.ic_fog
            WeatherIcons.CLOUDY -> R.drawable.ic_cloudy
            WeatherIcons.PARTLY_CLOUDY_DAY -> R.drawable.ic_partly_cloudy_day
            WeatherIcons.PARTLY_CLOUDY_NIGHT -> R.drawable.ic_partly_cloudy_night
            else -> R.drawable.ic_clear_day
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getFormattedDate(): String {
        val formatter = SimpleDateFormat(DATE_PATTERN)
        val date = Date(time)
        return formatter.format(date)
    }
}

data class LocationWeatherDetails(
    val latitude: String,
    val longitude: String,
    @SerializedName("currently") val weather: WeatherInfo
)
