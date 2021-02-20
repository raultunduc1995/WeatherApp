package com.example.weatherapp.ui.listeners

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import com.example.weatherapp.utils.closeSoftwareKeyboard

class ContainerOnTouchListener : View.OnTouchListener {
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(container: View?, event: MotionEvent?): Boolean {
        container?.closeSoftwareKeyboard()
        container?.clearFocus()
        return true
    }
}
