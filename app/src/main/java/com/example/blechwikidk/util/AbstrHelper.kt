package com.example.blechwikidk.util

import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

abstract class AbstrHelper {

    fun setTextWatcher(sucheTextbox: EditText){
        sucheTextbox.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                val sleep = when(s.length) {
                    1 -> 1000L
                    2,3 -> 700L
                    4,5 -> 500L
                    else -> 300L
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    afterontextChanged(s.toString())
                }, sleep)

            }
        })
    }

    //---------------------
    abstract fun afterontextChanged(suchstring: String)
}