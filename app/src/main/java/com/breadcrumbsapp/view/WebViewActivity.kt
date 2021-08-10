package com.breadcrumbsapp.view

import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import androidx.appcompat.app.AppCompatActivity
import com.breadcrumbsapp.databinding.WebivewLayoutBinding

class WebViewActivity:AppCompatActivity()
{

    private lateinit var binding: WebivewLayoutBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= WebivewLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // disabled the zoom function
        binding.webView.settings.setSupportZoom(false)
        binding.webView.settings.javaScriptCanOpenWindowsAutomatically = true
        binding.webView.settings.setGeolocationEnabled(true)
        binding.webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        binding.webView.requestFocus(View.FOCUS_DOWN)
        binding.webView.settings.domStorageEnabled = true

        val bundle: Bundle? = intent.extras
        val name: String? = bundle?.getString("urlString")

        if (name != null) {
            binding.webView.loadUrl(name)
        }
    }
}