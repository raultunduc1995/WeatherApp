package com.example.weatherapp.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.remove() {
    this.visibility = View.GONE
}

fun View.disable() {
    this.isEnabled = false
}

fun View.enable() {
    this.isEnabled = true
}

fun TextInputEditText.isValid(): Boolean {
    return !this.text.isNullOrEmpty()
}

fun AppCompatActivity.addFragment(container: Int, fragment: Fragment) {
    supportFragmentManager.beginTransaction()
        .add(container, fragment)
        .commit()
}

fun AppCompatActivity.replaceFragment(container: Int, fragment: Fragment) {
    supportFragmentManager.beginTransaction()
        .replace(container, fragment)
        .commit()
}

fun View.closeSoftwareKeyboard() {
    val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE)
    inputMethodManager?.let {
        (it as InputMethodManager).hideSoftInputFromWindow(windowToken, 0)
    }
}

fun RecyclerView.scrollSmoothlyToEnd() {
    adapter?.let {
        if (it.itemCount > 0) {
            this.smoothScrollToPosition(it.itemCount - 1)
        }
    }
}
