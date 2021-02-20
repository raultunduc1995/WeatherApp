package com.example.weatherapp.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weatherapp.repository.database.daos.LocalWeatherDao
import com.example.weatherapp.repository.database.domain.LocalWeatherInfo

@Database(entities = [LocalWeatherInfo::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun localWeatherDao(): LocalWeatherDao
}
