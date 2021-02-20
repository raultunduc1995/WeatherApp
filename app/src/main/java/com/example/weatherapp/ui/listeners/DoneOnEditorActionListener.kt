package com.example.weatherapp.ui.listeners

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.example.weatherapp.utils.closeSoftwareKeyboard

class DoneOnEditorActionListener : TextView.OnEditorActionListener {
    override fun onEditorAction(textView: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            textView?.closeSoftwareKeyboard()
            textView?.clearFocus()
            return true
        }
        return false
    }
}
