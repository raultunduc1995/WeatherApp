package com.example.weatherapp

import android.app.Application
import com.example.weatherapp.dependecy.AppComponent
import com.example.weatherapp.dependecy.AppModule
import com.example.weatherapp.dependecy.DaggerAppComponent

class App : Application() {
    val appComponent: AppComponent = DaggerAppComponent.builder()
        .appModule(AppModule(this))
        .build()
}
