package com.example.blechwikidk.util

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.widget.Toast
import com.example.blechwikidk.BuildConfig
import kotlinx.coroutines.MainScope
import androidx.core.content.edit

object SessionLib {

    const val IDENT = "BlechWikiDK"
    //diese drei Werte mit build.gradle synchron halten
    const val APPLICATIONID = "com.example.blechwikidk"
    const val VERSIONCODE = 37
    const val VERSIONNAME = "10.01.26"


    const val BASISURL = "http://pcportal.ddns.net/RestBlechWiki/api/"
    const val BASISURLPICBUCH = "http://pcportal.ddns.net/Bilder/BilderBuch/"
    var isOnline = ""
    var currentVersionName = BuildConfig.VERSION_NAME
    var isinitialized = "false"

    lateinit var sharedPref: SharedPreferences

    val mainScope = MainScope()

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
        sharedPref.edit {
            putString(vkey, vValue)
        }
    }

    fun getPref(vkey:String):String{
        return sharedPref.getString(vkey,"noname")?:""
    }
}