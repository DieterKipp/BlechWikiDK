package com.example.blechwikidk

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log.d
import androidx.appcompat.app.AppCompatActivity
import com.example.blechwikidk.util.*
import com.google.android.material.snackbar.Snackbar
import com.example.blechwikidk.databinding.UpdateBinding


class Update : AppCompatActivity() {

    private lateinit var binding: UpdateBinding

    companion object {
        const val PERMISSION_REQUEST_STORAGE = 0
    }

    private lateinit var downloadController: DownloadController
    private lateinit var downloadControllerAPI29: DownloadControllerAPI29


    private val filename = "${SessionLib.ident}/${SessionLib.ident}.apk"
    private val apkUrl = "http://pcportal.ddns.net/downloads/apk/$filename"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = UpdateBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //---------------------------------------------------


        val newVersionText =
            if (SessionLib.updateVersionName == SessionLib.currentVersionName) {
                //es gibt keine neue Versionsnummer
                "Neuinstallation mit ${SessionLib.currentVersionName} möglich"
            } else {
                "update auf Version: ${SessionLib.updateVersionName}"
            }
        binding.TextviewNewVersion.text = newVersionText
        val version = "bisher: ${SessionLib.currentVersionName}"
        binding.TextviewOldVersion.text = version
        //---------------------


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            downloadControllerAPI29 = DownloadControllerAPI29(this, apkUrl,filename)
        }else{
            downloadController = DownloadController(this, apkUrl,filename)
        }

        binding.ButtonStartDownload.setOnClickListener {

            // check storage permission granted if yes then start downloading file
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                downloadControllerAPI29.enqueueDownload()
            }else{

                checkStoragePermission()
            }
        }
    }

    private fun checkStoragePermission() {
        // Check if the storage permission has been granted
        if (checkSelfPermissionCompat(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            // start downloading
            downloadController.enqueueDownload()
        } else {
            // Permission is missing and must be requested.
            requestStoragePermission()
        }
    }



    private fun requestStoragePermission() {

        if (shouldShowRequestPermissionRationaleCompat(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            binding.updateLayout.showSnackbar(
                R.string.storage_access_required,
                Snackbar.LENGTH_INDEFINITE, R.string.ok
            ) {
                requestPermissionsCompat(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_STORAGE
                )
            }

        } else {
            requestPermissionsCompat(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_STORAGE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_STORAGE) {
            // Request for storage permission.
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // start downloading
                downloadController.enqueueDownload()
                d("Dieter", "onRequestPermissionsResult: enqueueDownload")
            } else {
                // Permission request was denied.
                binding.updateLayout.showSnackbar(R.string.storage_permission_denied, Snackbar.LENGTH_SHORT)
                d("Dieter", "onRequestPermissionsResult: storage_permission_denied")
            }
        }
    }


}