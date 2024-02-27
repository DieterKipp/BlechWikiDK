package com.example.blechwikidk

import android.os.Bundle
import android.view.WindowManager
import android.webkit.WebSettings.LOAD_NO_CACHE
import androidx.appcompat.app.AppCompatActivity
import com.example.blechwikidk.databinding.BuchWebviewerBinding

class WebViewer:AppCompatActivity() {

    private lateinit var binding: BuchWebviewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = BuchWebviewerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding.buchDetailsWebviewer.settings.builtInZoomControls = true
        //webviewrezepte.settings.displayZoomControls = true
        //hält die controls sichtbar


        val browseURL = intent.getStringExtra("browseURL")!!

        binding.buchDetailsTvUrl.text = browseURL

        binding.buchDetailsWebviewer.settings.useWideViewPort = true
        binding.buchDetailsWebviewer.settings.loadWithOverviewMode = true

        //Log.d("Dieter", "webview browseURL: $browseURL")
        binding.buchDetailsWebviewer.settings.cacheMode = LOAD_NO_CACHE
        binding.buchDetailsWebviewer.loadUrl(browseURL)
    }

}