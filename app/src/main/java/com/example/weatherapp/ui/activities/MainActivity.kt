package com.example.weatherapp.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.App
import com.example.weatherapp.R
import com.example.weatherapp.dependecy.EntryComponent
import com.example.weatherapp.ui.fragments.EntryPointFragment
import com.example.weatherapp.utils.addFragment

class MainActivity : AppCompatActivity() {

    lateinit var entryComponent: EntryComponent

    private fun attachEntryPointFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            return
        }
        addFragment(R.id.container, EntryPointFragment())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        entryComponent = (applicationContext as App)
            .appComponent.entryComponent().create()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        attachEntryPointFragment(savedInstanceState)
    }
}
