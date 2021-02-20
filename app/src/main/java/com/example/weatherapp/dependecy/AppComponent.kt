package com.example.weatherapp.dependecy

import com.example.weatherapp.ui.fragments.EntryPointFragment
import com.example.weatherapp.ui.fragments.WeatherDetailsFragment
import dagger.Component
import dagger.Module
import dagger.Subcomponent
import javax.inject.Scope
import javax.inject.Singleton

@Scope
@Retention(value = AnnotationRetention.RUNTIME)
annotation class ActivityScope

@ActivityScope
@Subcomponent
interface EntryComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): EntryComponent
    }

    fun inject(fragment: EntryPointFragment)
    fun inject(fragment: WeatherDetailsFragment)
}

@Module(subcomponents = [EntryComponent::class])
class SubcomponentModule

@Singleton
@Component(
    modules = [
        AppModule::class,
        DatabaseModule::class,
        DarkSkyNetworkModule::class,
        GeocoderNetworkModule::class,
        SubcomponentModule::class
    ]
)
interface AppComponent {
    fun entryComponent(): EntryComponent.Factory
}
