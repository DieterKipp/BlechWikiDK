package com.example.blechwikidk.util

import android.app.DownloadManager
import android.content.Context
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.os.Environment.getExternalStoragePublicDirectory
import android.widget.Toast
import androidx.core.net.toUri
import com.example.blechwikidk.R
import java.io.File


class DownloadController(private val context: Context, private val url: String, private val filename:String) {


    fun enqueueDownload() {
        val destinationString =  getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).toString() + "/" + filename

        val file = File(destinationString)

        if (file.exists()) file.delete()

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = url.toUri()
        val request = DownloadManager.Request(downloadUri)
        request.setMimeType("application/vnd.android.package-archive")
        request.setTitle(context.getString(R.string.title_file_download))
        request.setDescription(context.getString(R.string.downloading))

        // set destination
        request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS,filename)

        // Enqueue a new download and same the referenceId
        downloadManager.enqueue(request)
        Toast.makeText(context, context.getString(R.string.downloading), Toast.LENGTH_LONG)
            .show()
    }

}
