package com.example.blechwikidk

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.os.Environment.getExternalStoragePublicDirectory
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.blechwikidk.databinding.UpdateBinding
import com.example.blechwikidk.util.DownloadController
import com.example.blechwikidk.util.SessionLib
import java.io.File


class Update : AppCompatActivity() {

    private lateinit var binding:UpdateBinding

    private lateinit var downloadController: DownloadController

    private val filename = "${SessionLib.IDENT}/${SessionLib.IDENT}.apk"
    private val apkUrl = "https://teddymuetze.ddns.net/downloads/apk/$filename"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = UpdateBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //---------------------------------------------------

        val versionNamefromServer = intent.getStringExtra("versionNamefromServer")
        val versionCodefromServer = intent.getIntExtra("versionCodefromServer",0)

        val newVersionText =
            if (versionCodefromServer == 99) {
                //Aufruf von "update" aus dem menue
                "Neuinstallation möglich"
            } else {
                "update auf Version: $versionCodefromServer vom $versionNamefromServer"
            }
        binding.TextviewNewVersion.text = newVersionText
        val version = "bisherige Version: ${SessionLib.VERSIONCODE} vom ${SessionLib.VERSIONNAME}"
        binding.TextviewOldVersion.text = version
        //---------------------

        downloadController = DownloadController(this, apkUrl,filename)

        setListeners()
        setBroadcastReceiverForDownloadready()
        binding.ButtonInstallation.visibility = View.GONE
    }

    private fun setListeners() {
        binding.ButtonStartDownload.setOnClickListener {
            downloadController.enqueueDownload()
        }

        binding.ButtonInstallation.setOnClickListener {
            installation()
        }

    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
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
        //ab api33
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.registerReceiver(br, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                RECEIVER_EXPORTED
            )
        }else{
            this.registerReceiver(br, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }

    }

    private fun installation(){
        val destinationString =  getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).toString() + "/" + filename
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