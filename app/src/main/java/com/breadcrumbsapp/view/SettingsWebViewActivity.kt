package com.breadcrumbsapp.view

import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import androidx.appcompat.app.AppCompatActivity
import com.breadcrumbsapp.R
import com.breadcrumbsapp.databinding.SettingsScreenWebviewBinding
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.settings_screen_webview.*

class SettingsWebViewActivity:AppCompatActivity()
{

    private lateinit var binding:SettingsScreenWebviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= SettingsScreenWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        webView.settings.setSupportZoom(false)
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.setGeolocationEnabled(true)
        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        webView.requestFocus(View.FOCUS_DOWN)
        webView.settings.domStorageEnabled = true


        val titleIcon=intent.extras?.get("terms") as String

        if(titleIcon == "yes")
        {
            Glide.with(applicationContext).load(R.drawable.settings_terms_service).into(title_icon)
            webView_title.text="TERMS OF SERVICE"
            binding.webView.loadUrl(resources.getString(R.string.terms_of_url))
        }
        else
        {
            Glide.with(applicationContext).load(R.drawable.settings_privacy_policy).into(title_icon)
            webView_title.text="PRIVACY POLICY"
            binding.webView.loadUrl(resources.getString(R.string.privacy_policy_url))
        }


        webView_title_screen_backButton.setOnClickListener {
            finish()
        }

    }
}