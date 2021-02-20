package com.example.weatherapp.repository.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.repository.database.domain.LocalWeatherInfo
import io.reactivex.Flowable

@Dao
interface LocalWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg localWeathers: LocalWeatherInfo)

    @Query("SELECT * FROM LocalWeatherInfo")
    fun getAll(): Flowable<List<LocalWeatherInfo>>
}
