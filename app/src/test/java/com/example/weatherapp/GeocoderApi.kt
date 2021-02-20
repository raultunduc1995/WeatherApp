package com.example.weatherapp

import com.example.weatherapp.api.MockGeocoderApi
import com.example.weatherapp.repository.geocoder.api.GeocoderApiInterface
import com.example.weatherapp.repository.geocoder.api.domain.LocationData
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import java.net.HttpURLConnection

class GeocoderApi {
    private val mockGeocoderApiService = MockGeocoderApi()
    private lateinit var geocoderApiInterface: GeocoderApiInterface

    @Before
    fun setup() {
        geocoderApiInterface = mockGeocoderApiService.getApi()
    }

    @After
    fun teardown() {
        mockGeocoderApiService.shutdownService()
    }

    @Test
    fun testApiHttpNotFound() {
        try {
            geocoderApiInterface.getLocation("some dummy location").blockingFirst()
            Assert.assertTrue(false)
        } catch (exception: HttpException) {
            Assert.assertEquals(HttpURLConnection.HTTP_NOT_FOUND, exception.code())
        }
    }

    @Test
    fun testGermanyLocation() {
        try {
            val response: LocationData = geocoderApiInterface.getLocation("").blockingFirst()
            val firstResult = response.results.first()
            Assert.assertEquals("Germany", firstResult.component.country)
            Assert.assertEquals("Weimar", firstResult.component.city)
            Assert.assertEquals(50.977435, firstResult.geometry.lat, 0.0)
            Assert.assertEquals(11.3292894, firstResult.geometry.lng, 0.0)
        } catch (exception: Exception) {
            Assert.assertTrue(exception.message, false)
        }
    }
}
