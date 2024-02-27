package com.example.blechwikidk.util

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.blechwikidk.BuildConfig
import com.example.blechwikidk.Update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.URL

object SessionLib {

    const val ident = "BlechWikiDK"
    const val RestBasisURL = "http://pcportal.ddns.net/RestBlechWiki/api/"
    const val BasisURLPicBuch = "http://pcportal.ddns.net/Bilder/BilderBuch/"
    var isOnline = ""
    var updateVersionName = ""
    var currentVersionName = BuildConfig.VERSION_NAME
    var isinitialized = "false"

    lateinit var sharedPref: SharedPreferences

    private val mainScope = MainScope()

    fun checkforNewVersion(ctx:Context) {
        var startindex: Int
        var endindex: Int
        var versioncode = 0
        val currentVersionCode = BuildConfig.VERSION_CODE


        if(isOnline=="true"){
            // asynchronously read html from url
            // io dispatcher for background thread network operation
            val json = mainScope.async(Dispatchers.IO) {
                try {
                    URL("http://teddymuetze.ddns.net/downloads/apk/$ident/output-metadata.json").readText(charset("UTF-8"))
                } catch (e: IOException) {
                    // catch exception while reading it
                    "Error....\n\n$e"
                }
            }
            //---
            // main dispatcher for interacting with ui views
            mainScope.launch(Dispatchers.Main) {
                val linelist = json.await().lines()
                for(i in linelist.indices){
                    if(linelist[i].contains("versionCode",true)){
                        startindex = linelist[i].indexOf(":") + 2
                        endindex = linelist[i].length - 1
                        if (startindex != 0 && startindex < endindex) {
                            versioncode = linelist[i].substring(startindex, endindex).toInt()
                        }
                    }
                    if(linelist[i].contains("versionName",true)){
                        startindex = linelist[i].indexOf(":") + 3
                        endindex = linelist[i].length - 2
                        if (startindex != 0 && startindex < endindex) {
                            updateVersionName = linelist[i].substring(startindex, endindex)
                        }
                    }
                }

                if (currentVersionCode < versioncode) {

                    val aDBuilder = AlertDialog.Builder(ctx)
                        .setTitle("updaten")
                        .setMessage("Es gibt eine neue updateversion! $updateVersionName")
                        .setPositiveButton("OK!") { _, _ ->
                            val intent = Intent(ctx, Update::class.java)
                            val mBundle = Bundle()
                            ContextCompat.startActivity(ctx, intent,mBundle)
                        }
                        .setNegativeButton("jetzt nicht!") { _, _ ->
                            Toast.makeText(ctx, "dann nächstes Mal...", Toast.LENGTH_SHORT)
                                .show()
                        }
                    val dialog = aDBuilder.create()
                    dialog.show()
                }
            }
        }
    }

    fun getOnlineStatus(ctx: Context) {

        val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetwork
        isOnline = if (activeNetwork == null) {
            "false"
        } else {
            "true"
        }
    }

    fun dialoganzeigen(ctx:Context,msg:String,btntext:String,titel:String){
        val adbuilder = androidx.appcompat.app.AlertDialog.Builder(ctx)
        adbuilder.setTitle(titel)
        adbuilder.setMessage(msg)
        adbuilder.setPositiveButton(btntext) { _, _ ->
            Toast.makeText(
                ctx,
                "verstanden :-)",
                Toast.LENGTH_SHORT
            ).show()
        }
        val dialog = adbuilder.create()
        dialog.show()
    }

    fun setPref(vkey:String,vValue:String){
        val editor = sharedPref.edit()
        editor.putString(vkey, vValue).apply()
    }

    fun getPref(vkey:String):String{
        return sharedPref.getString(vkey,"noname")?:""
    }
}