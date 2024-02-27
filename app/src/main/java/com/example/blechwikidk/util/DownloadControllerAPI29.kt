package com.example.blechwikidk.util

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.os.Environment.getExternalStoragePublicDirectory
import android.util.Log.d
import android.widget.Toast
import androidx.core.content.FileProvider
import  com.example.blechwikidk.BuildConfig
import  com.example.blechwikidk.R
import java.io.File


class DownloadControllerAPI29(private val context: Context, private val url: String,private val filename:String) {

    fun enqueueDownload() {
        val destinationString =  getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).toString() + "/" + filename

        val file = File(destinationString)
        //var vorhanden = file.exists()
        if (file.exists()) file.delete()

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(url)
        val request = DownloadManager.Request(downloadUri)
        request.setMimeType("application/vnd.android.package-archive")
        request.setTitle(context.getString(R.string.title_file_download))
        request.setDescription(context.getString(R.string.downloading))

        // set destination
        request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS,filename)

        showInstallOption(destinationString)
        // Enqueue a new download and same the referenceId
        downloadManager.enqueue(request)
        Toast.makeText(context, context.getString(R.string.downloading), Toast.LENGTH_LONG)
            .show()
    }
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun showInstallOption(destination: String) {

        // set BroadcastReceiver to install app when .apk is downloaded
        val br = object : BroadcastReceiver() {
            override fun onReceive(
                context: Context,
                intent: Intent
            ) {
                val contentUri = FileProvider.getUriForFile(
                    context,
                    BuildConfig.APPLICATION_ID + ".provider",
                    File(destination)
                )
                //SDK needs to be >= 27 :: >= Build.VERSION_CODES.N
                val installintent = Intent(Intent.ACTION_VIEW)
                installintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                installintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                installintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                installintent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
                installintent.data = contentUri
                d("Dieter", "downloadcontroller startinstall: $contentUri")
                context.startActivity(installintent)
                context.unregisterReceiver(this)
                //finish()
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(br, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_EXPORTED)
        }else{
            context.registerReceiver(br, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }

    }
}
