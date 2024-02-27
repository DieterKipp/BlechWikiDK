package com.example.blechwikidk

import android.os.Bundle
import android.util.Log.d
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.blechwikidk.databinding.EgliederTextBinding
import com.example.blechwikidk.util.SessionLib
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.io.IOException
import java.net.URL

class EGLiederText:AppCompatActivity() {

    private lateinit var binding: EgliederTextBinding
    private var vText = ""
    private val mainScope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = EgliederTextBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            val localclassname = localClassName
            title = localclassname
        }

        //------------------------------------------------

        fetchdataFromURL()

    }

    private fun fetchdataFromURL() {
        // asynchronously read html from url
        // io dispatcher for background thread network operation

        val vIx = intent.getStringExtra("vIx")
        val uRLString = SessionLib.RestBasisURL + "EGText?EGIx=$vIx"

        //d("Dieter", "fetchdatafromurl $uRLString")
        //

        val json = mainScope.async(Dispatchers.IO) {
            try {
                URL(uRLString).readText(charset("UTF-8"))

            } catch (e: IOException) {
                // catch exception while reading it
                "Error....\n\n$e"
            }
        }
        //---
        // main dispatcher for interacting with ui views
        mainScope.launch(Dispatchers.Main) {
            d("Dieter",json.await())
            vText = Gson().fromJson(json.await(), String::class.java).toString()

            vText = vText.replace("\r\n", " \n\n", false)
            binding.EGLiedTextTextViewMulti.setText(vText)
            //d("Dieter",vText)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return true
    }
}