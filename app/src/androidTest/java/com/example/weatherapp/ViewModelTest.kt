package com.example.weatherapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weatherapp.dependecy.DarkSkyNetworkModule
import com.example.weatherapp.dependecy.GeocoderNetworkModule
import com.example.weatherapp.repository.UnknownLocation
import com.example.weatherapp.repository.WeatherRepository
import com.example.weatherapp.repository.darksky.api.DarkSkyApiInterface
import com.example.weatherapp.repository.database.AppDatabase
import com.example.weatherapp.repository.database.daos.LocalWeatherDao
import com.example.weatherapp.repository.geocoder.api.GeocoderApiInterface
import com.example.weatherapp.ui.viewmodels.MainViewModel
import com.example.weatherapp.utils.Mapper
import com.example.weatherapp.utils.WeatherDataMapper
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
@SmallTest
class ViewModelTest {
    companion object {
        private const val CITY = "Weimar"
        private const val COUNTRY = "Germany"
    }

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private lateinit var geocoderApi: GeocoderApiInterface
    private lateinit var darkSyInterface: DarkSkyApiInterface
    private lateinit var databases: AppDatabase
    private lateinit var localWeatherDao: LocalWeatherDao
    private lateinit var repository: WeatherRepository
    private lateinit var mapper: Mapper
    private lateinit var viewModel: MainViewModel

    private fun initDatabase() {
        databases = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
        localWeatherDao = databases.localWeatherDao()
    }

    @Before
    fun setup() {
        geocoderApi = GeocoderNetworkModule().getApiInterface()
        darkSyInterface = DarkSkyNetworkModule().getApiInterface()
        initDatabase()
        mapper = WeatherDataMapper()
        repository = WeatherRepository(
            localWeatherDao,
            geocoderApi,
            darkSyInterface,
            mapper
        )
        viewModel = MainViewModel(repository)
    }

    @After
    @Throws(IOException::class)
    fun teardown() {
        databases.close()
    }

    @Test
    fun testEmptyFields() {
        try {
            viewModel.getLocationDetails("", "")
        } catch (error: Exception) {
            val context = ApplicationProvider.getApplicationContext<Context>()
            Assert.assertTrue(error is UnknownLocation)
            Assert.assertEquals(
                context.getString(R.string.unknown_details),
                error.message
            )
        }
    }

    @Test
    fun testRandomCountryCity() {
        try {
            viewModel.getLocationDetails("asdf", "asd")
        } catch (error: Exception) {
            Assert.assertTrue(error is UnknownLocation)
            Assert.assertEquals(
                context.getString(R.string.unknown_details),
                error.message
            )
        }
    }

    @Test
    fun testLocationDetailsAttribute() {
        try {
            val result = viewModel.getLocationDetails(CITY, COUNTRY).blockingFirst()
            Assert.assertEquals(result.geometry.lat, viewModel.locationDetails?.geometry?.lat)
            Assert.assertEquals(result.geometry.lng, viewModel.locationDetails?.geometry?.lng)
            Assert.assertEquals(
                result.component.city,
                viewModel.locationDetails?.component?.city
            )
            Assert.assertEquals(
                result.component.country,
                viewModel.locationDetails?.component?.country
            )
        } catch (error: Exception) {
            Assert.assertTrue("Test failed: ${error.message}", false)
        }
    }

    @Test
    fun testValidDataFlow() {
        try {
            viewModel.getLocationDetails(CITY, COUNTRY).blockingFirst()
            viewModel.getAndSaveSelectedLocationWeather().blockingFirst()
            val databaseList = viewModel.getAllWeatherDetails().blockingFirst()
            databaseList.first().apply {
                Assert.assertEquals(CITY, this.city)
                Assert.assertEquals(COUNTRY, this.country)
                Assert.assertEquals(50.977435, this.latitude, 0.01)
                Assert.assertEquals(11.3292894, this.longitude, 0.01)
            }
        } catch (error: Exception) {
            Assert.assertTrue("Test failed: ${error.message}", false)
        }
    }
}
