package com.example.blechwikidk.util

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log.d
import android.widget.Toast
import androidx.room.Update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import com.example.blechwikidk.util.SessionLib.IDENT
import com.example.blechwikidk.util.SessionLib.VERSIONCODE
import com.example.blechwikidk.util.SessionLib.isOnline
import com.example.blechwikidk.util.SessionLib.mainScope
import java.io.IOException
import java.net.URL

class UpdateHelper {


    fun checkforNewVersion(ctx: Context) {
        var startindex: Int
        var endindex: Int
        var versionCodefromServer = 0
        var versionNamefromServer = ""


        if(isOnline =="true"){
            // asynchronously read html from url
            // io dispatcher for background thread network operation
            val json = mainScope.async(Dispatchers.IO) {
                try {
                    URL("https://teddymuetze.ddns.net/downloads/apk/$IDENT/output-metadata.json").readText(charset("UTF-8"))
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
                            versionCodefromServer = linelist[i].substring(startindex, endindex).toInt()
                        }
                    }
                    if(linelist[i].contains("versionName",true)){
                        startindex = linelist[i].indexOf(":") + 3
                        endindex = linelist[i].length - 2
                        if (startindex != 0 && startindex < endindex) {
                            versionNamefromServer = linelist[i].substring(startindex, endindex)
                        }
                    }
                }


                d("Dieter","$VERSIONCODE:$versionCodefromServer")
                if (VERSIONCODE < versionCodefromServer) {

                    val aDBuilder = AlertDialog.Builder(ctx)
                        .setTitle("updaten")
                        .setMessage("Es gibt eine neue updateversion! Version $versionCodefromServer vom $versionNamefromServer")
                        .setPositiveButton("OK!") { _, _ ->
                            val intent = Intent(ctx, Update::class.java)
                            //val mBundle = Bundle()
                            intent.putExtra("versionCodefromServer",versionCodefromServer)
                            intent.putExtra("versionNamefromServer",versionNamefromServer)
                            ctx.startActivity(intent)
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
}