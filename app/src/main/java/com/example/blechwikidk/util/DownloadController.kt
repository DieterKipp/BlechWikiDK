package com.example.blechwikidk.util

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.blechwikidk.BuildConfig
import com.example.blechwikidk.R
import java.io.File

class DownloadController(private val context: Context, private val url: String,private val filename:String) {

    companion object {
        private const val FILE_BASE_PATH = "file://"
        private const val MIME_TYPE = "application/vnd.android.package-archive"
        private const val PROVIDER_PATH = ".provider"
    }


    fun enqueueDownload() {

        var destination =
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/"

        destination += filename

        val uri = Uri.parse("$FILE_BASE_PATH$destination")

        val file = File(destination)
        if (file.exists()) file.delete()

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(url)
        val request = DownloadManager.Request(downloadUri)
        request.setMimeType(MIME_TYPE)
        request.setTitle(context.getString(R.string.title_file_download))
        request.setDescription(context.getString(R.string.downloading))

        // set destination
        request.setDestinationUri(uri)

        showInstallOption(destination)
        // Enqueue a new download and same the referenceId
        downloadManager.enqueue(request)
        Toast.makeText(context, context.getString(R.string.downloading), Toast.LENGTH_LONG)
            .show()

    }
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun showInstallOption(
        destination: String
    ) {

        // set BroadcastReceiver to install app when .apk is downloaded
        val br = object : BroadcastReceiver() {
            override fun onReceive(
                context: Context,
                intent: Intent
            ) {
                val contentUri = FileProvider.getUriForFile(
                    context,
                    BuildConfig.APPLICATION_ID + PROVIDER_PATH,
                    File(destination)
                )
                //SDK needs to be >= 27 :: >= Build.VERSION_CODES.N
                val intentinstall = Intent(Intent.ACTION_VIEW)
                intentinstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intentinstall.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intentinstall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intentinstall.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
                intentinstall.data = contentUri
                context.startActivity(intentinstall)
                context.unregisterReceiver(this)
                // finish()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(br, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_EXPORTED)
        }else{
            context.registerReceiver(br, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }
    }
}
