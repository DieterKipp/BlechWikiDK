package com.example.blechwikidk

import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.os.Environment.getExternalStoragePublicDirectory
import android.util.Log
import android.util.Log.d
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.example.blechwikidk.databinding.UpdateBinding
import com.example.blechwikidk.util.SessionLib
import java.io.File
import java.time.LocalDate


class Update : AppCompatActivity() {

    private lateinit var binding: UpdateBinding

    //private var filename = ""
    private var filenametoday = ""
    private val apkUrl = "https://teddymuetze.ddns.net/downloads/apk/${SessionLib.IDENT}/${SessionLib.IDENT}.apk"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = UpdateBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        SessionLib.setInsets(view)

        //---------------------------------------------------

        val versionNamefromServer = intent.getStringExtra("versionNamefromServer")
        val versionCodefromServer = intent.getIntExtra("versionCodefromServer",0)

        val newVersionText =
            if (versionCodefromServer == 99) {
                //Aufruf von "update" aus dem menue - intent versionCodefromServer == 99 setzen
                "Neuinstallation möglich"
            } else {
                "update auf Version: $versionCodefromServer vom $versionNamefromServer"
            }
        binding.TextviewNewVersion.text = newVersionText
        val version = "bisherige Version: ${SessionLib.VERSIONCODE} vom ${SessionLib.VERSIONNAME}"
        binding.TextviewOldVersion.text = version
        //---------------------


        setListeners()
        setBroadcastReceiverForDownloadready()
        binding.ButtonInstallation.visibility = View.GONE

        val thisday= LocalDate.now().year * 10000 + LocalDate.now().monthValue * 100 + LocalDate.now().dayOfMonth
        filenametoday = "${SessionLib.IDENT}/${SessionLib.IDENT}$thisday.apk"

        deleteoldfile()
    }

    private fun deleteoldfile() {

        val destinationString =  getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).toString() + "/" + filenametoday

        val file = File(destinationString)

        if (file.exists()){
            if(!file.delete()){
                SessionLib.dialoganzeigen(this,"ich kann die alte Datei - $filenametoday - nicht löschen! bitte manuell erledigen","verstanden","Fehler!")

            }
        }
        d("Dieter","deleteoldfile")
    }

    private fun setListeners() {
        binding.ButtonStartDownload.setOnClickListener {
            //downloadController.enqueueDownload()
            enqueueDownload()
        }

        binding.ButtonInstallation.setOnClickListener {
            installation()
        }

    }


    private fun setBroadcastReceiverForDownloadready() {

        // set BroadcastReceiver to install app when .apk is downloaded
        //        // dafür muss im Manifest 'REQUEST_INSTALL_PACKAGES' eingetragen sein
        //        // und der fileprovider
        //        // und die XML 'file_provider_paths' muss angelegt sein

        val br = object : BroadcastReceiver() {
            override fun onReceive(
                context: Context,
                intent: Intent
            ) {
//                SessionLib.dialoganzeigen(context,"du kannst jetzt die Installation starten !","verstanden","download fertig")
//                binding.ButtonInstallation.visibility = View.VISIBLE
                context.unregisterReceiver(this)

                //probieren, ob das läuft - ansonsten dialoganzeigen s.o.
                installation()
            }
        }

        this.registerReceiver(br, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            RECEIVER_EXPORTED
        )

    }

    fun enqueueDownload() {

        val downloadManager = this.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = apkUrl.toUri()
        val request = DownloadManager.Request(downloadUri)
        request.setMimeType("application/vnd.android.package-archive")
        request.setTitle(this.getString(R.string.title_file_download))
        request.setDescription(this.getString(R.string.downloading))

        // set destination

        request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS,filenametoday)

        // Enqueue a new download and same the referenceId
        downloadManager.enqueue(request)
        Toast.makeText(this, this.getString(R.string.downloading), Toast.LENGTH_LONG)
            .show()

    }

    private fun installation(){
        val destinationString =  getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).toString() + "/" + filenametoday
        val contentUri = FileProvider.getUriForFile(
            this,
            SessionLib.APPLICATIONID + ".provider",
            File(destinationString)
        )
        val installintent = Intent(Intent.ACTION_VIEW)
        installintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        installintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        installintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        installintent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
        installintent.data = contentUri
        try {
            startActivity(installintent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Log.e("Dieter", "Error in opening the file! : $e")
        }
    }

}