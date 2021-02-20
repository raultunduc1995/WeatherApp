package com.example.weatherapp.api

import com.example.weatherapp.repository.darksky.api.DarkSkyApiInterface
import com.example.weatherapp.utils.FileUtils
import com.example.weatherapp.utils.doSafely
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

class MockDarkSkyApi {
    companion object {
        private const val TEST_FILE = "/darksky/germany_weather.json"
        const val LAT = 0.0
        const val LNG = 0.0
    }

    private val mockWebServer = MockWebServer()

    fun getApi(): DarkSkyApiInterface {
        mockWebServer.dispatcher = DarkSkyDispatcher()
        mockWebServer.start()
        return Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
            .create(DarkSkyApiInterface::class.java)
    }

    fun shutdown() {
        doSafely { mockWebServer.shutdown() }
    }

    inner class DarkSkyDispatcher : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            return when (request.path) {
                "/forecast/${LAT},${LNG}" -> {
                    MockResponse()
                        .setResponseCode(HttpURLConnection.HTTP_OK)
                        .setBody(FileUtils.readFromResourceFile(TEST_FILE))
                }
                else -> MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
            }
        }
    }
}
