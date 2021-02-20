package com.example.weatherapp.api

import com.example.weatherapp.repository.geocoder.api.GeocoderApiInterface
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

class MockGeocoderApi {
    companion object {
        private const val TEST_FILE = "/geocoder/germany.json"
    }

    private val mockWebServer = MockWebServer()

    fun getApi(): GeocoderApiInterface {
        mockWebServer.dispatcher = GeocoderDispatcher()
        mockWebServer.start()
        return Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
            .create(GeocoderApiInterface::class.java)
    }

    fun shutdownService() {
        doSafely { mockWebServer.shutdown() }
    }

    inner class GeocoderDispatcher : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            return when (request.path) {
                "/geocode/v1/json?q=" -> {
                    MockResponse()
                        .setResponseCode(HttpURLConnection.HTTP_OK)
                        .setBody(FileUtils.readFromResourceFile(TEST_FILE))
                }
                else -> MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
            }
        }
    }
}
