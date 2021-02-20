package com.example.weatherapp

import com.example.weatherapp.api.MockDarkSkyApi
import com.example.weatherapp.repository.darksky.api.DarkSkyApiInterface
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import java.net.HttpURLConnection

class DarkSkyApi {
    private val mockDarkSkyApi = MockDarkSkyApi()
    private lateinit var darkSkyApi: DarkSkyApiInterface

    @Before
    fun setup() {
        darkSkyApi = mockDarkSkyApi.getApi()
    }

    @After
    fun teardown() {
        mockDarkSkyApi.shutdown()
    }

    @Test
    fun testApiHttpNotFound() {
        try {
            darkSkyApi.getWeatherDetails(1.1, 1.1).blockingFirst()
            Assert.assertTrue(false)
        } catch (exception: HttpException) {
            Assert.assertEquals(HttpURLConnection.HTTP_NOT_FOUND, exception.code())
        }
    }

    @Test
    fun testDarkSkyApi() {
        try {
            val response =
                darkSkyApi.getWeatherDetails(MockDarkSkyApi.LAT, MockDarkSkyApi.LNG).blockingFirst()
            Assert.assertEquals(11.03, response.weather.temperature, 0.0)
            Assert.assertEquals("cloudy", response.weather.icon)
        } catch (error: Exception) {
            Assert.assertTrue(error.message, false)
        }
    }
}
