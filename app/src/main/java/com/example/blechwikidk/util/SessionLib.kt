package com.example.blechwikidk.util

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.util.Log.d
import android.view.View
import android.widget.Toast
import kotlinx.coroutines.MainScope
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

object SessionLib {

    const val IDENT = "BlechWikiDK"
    //diese drei Werte mit build.gradle synchron halten
    const val APPLICATIONID = "com.example.blechwikidk"
    const val VERSIONCODE = 39
    const val VERSIONNAME = "12.10.26"


    const val BASISURL = "https://teddymuetze.ddns.net/RestBlechWiki/api/"
    const val BASISURLPICBUCH = "https://teddymuetze.ddns.net/Bilder/BilderBuch/"
    var isOnline = ""
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

    fun orientation(ctx:Context):String{
        val orientation =  ctx.resources.configuration.orientation
        return if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            "landscape"
        } else {
            "portrait"
        }
    }

    fun isTablet(ctx: Context): Boolean {
        return ctx.resources.configuration.smallestScreenWidthDp >= 600
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

    fun setInsets(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.displayCutout()
            )
            d("Dieter","$bars")
            v.updatePadding(
                left = bars.left,
                top = bars.top,
                right = bars.right,
                bottom = bars.bottom,
            )
            WindowInsetsCompat.CONSUMED
        }
    }
}