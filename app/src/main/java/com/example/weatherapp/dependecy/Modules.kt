package com.example.weatherapp.dependecy

import android.content.Context
import androidx.room.Room
import com.example.weatherapp.App
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.repository.darksky.api.DarkSkyApiInterface
import com.example.weatherapp.repository.database.AppDatabase
import com.example.weatherapp.repository.database.daos.LocalWeatherDao
import com.example.weatherapp.repository.geocoder.api.GeocoderApiInterface
import com.example.weatherapp.utils.Mapper
import com.example.weatherapp.utils.WeatherDataMapper
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class AppModule(app: App) {
    private val application: App = app

    @Singleton
    @Provides
    fun getContext(): Context = application.applicationContext

    @Singleton
    @Provides
    fun getMapper(): Mapper = WeatherDataMapper()
}

@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun getLocalWeatherDao(appDatabase: AppDatabase): LocalWeatherDao {
        return appDatabase.localWeatherDao()
    }

    @Singleton
    @Provides
    fun getRoomDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "WeatherDB").build()
    }
}

@Module
class GeocoderNetworkModule {
    companion object {
        private const val KEY = "key"
        private const val PRETTY = "pretty"
    }

    @Singleton
    @Provides
    fun getApiInterface(): GeocoderApiInterface {
        val httpLoggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
        val parametersInterceptor = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val original = chain.request()
                val originalUrl = original.url

                val url = originalUrl.newBuilder()
                    .addQueryParameter(KEY, BuildConfig.GEOCODER_API_KEY)
                    .addQueryParameter(PRETTY, "1")
                    .build()

                val requestBuilder = original.newBuilder().url(url)
                val request = requestBuilder.build()
                return chain.proceed(request)
            }
        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(parametersInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.GEOCODER_API)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()

        return retrofit.create(GeocoderApiInterface::class.java)
    }
}

@Module
class DarkSkyNetworkModule {
    companion object {
        private const val UNITS = "units"
        private const val EXCLUDE = "exclude"
        private const val API_KEY_PATH_SEGMENT_INDEX = 1
    }

    @Singleton
    @Provides
    fun getApiInterface(): DarkSkyApiInterface {
        val httpLoggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
        val parametersInterceptor = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val original = chain.request()
                val originalUrl = original.url

                val url = originalUrl.newBuilder()
                    .setPathSegment(API_KEY_PATH_SEGMENT_INDEX, BuildConfig.DARK_SKY_API_KEY)
                    .addPathSegment(originalUrl.pathSegments.last())
                    .addQueryParameter(UNITS, "ca")
                    .addQueryParameter(EXCLUDE, "minutely,hourly,daily")
                    .build()

                val requestBuilder = original.newBuilder().url(url)
                val request = requestBuilder.build()
                return chain.proceed(request)
            }
        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(parametersInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.DARK_SKY_API)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()

        return retrofit.create(DarkSkyApiInterface::class.java)
    }
}
