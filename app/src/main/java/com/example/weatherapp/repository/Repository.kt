package com.example.weatherapp.repository

import com.example.weatherapp.repository.darksky.api.DarkSkyApiInterface
import com.example.weatherapp.repository.darksky.api.domain.LocationWeatherDetails
import com.example.weatherapp.repository.database.daos.LocalWeatherDao
import com.example.weatherapp.repository.geocoder.api.GeocoderApiInterface
import com.example.weatherapp.repository.geocoder.api.domain.Result
import com.example.weatherapp.utils.Mapper
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val localWeatherDao: LocalWeatherDao,
    private val geocoderApiInterface: GeocoderApiInterface,
    private val darkSkyApiInterface: DarkSkyApiInterface,
    private val mapper: Mapper
) {

    fun fetchLocationCoordinates(city: String, country: String): Flowable<Result> {
        val hasCountryDetails: (Result) -> Boolean = { locationDetails ->
            val component = locationDetails.component

            ((component.city != null && component.city.contains(city))
                    || (component.town != null && component.town.contains(city))
                    || (component.county != null && component.county.contains(city))
                    || (component.state != null && component.state.contains(city)))
                    && country == component.country
        }
        val filterLocationDetails: (List<Result>) -> Result =
            { results ->
                val res = results.filter { hasCountryDetails(it) }
                if (res.isEmpty()) {
                    throw UnknownLocation("No location details found")
                }
                res.first()
            }
        val query = "$city $country"

        return geocoderApiInterface.getLocation(query)
            .delay(1000, TimeUnit.MILLISECONDS)
            .map { filterLocationDetails(it.results) }
    }

    fun getAllSavedWeatherData() =
        localWeatherDao.getAll()

    fun getAndSaveLocationWeather(location: Result): Flowable<LocationWeatherDetails> {
        val saveWeatherDetailsInDB: (locationWeatherDetails: LocationWeatherDetails) -> Unit =
            { locationWeatherDetails ->
                localWeatherDao.insertAll(
                    mapper.mapLocalWeatherDetailsToDBEntity(
                        location,
                        locationWeatherDetails.weather
                    )
                )
            }

        return darkSkyApiInterface.getWeatherDetails(location.geometry.lat, location.geometry.lng)
            .map { locationWeatherDetails ->
                saveWeatherDetailsInDB(locationWeatherDetails)
                locationWeatherDetails
            }
            .delay(1500, TimeUnit.MILLISECONDS)
    }
}
