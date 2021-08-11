package com.breadcrumbsapp.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.breadcrumbsapp.R
import com.breadcrumbsapp.databinding.HowToPlayLayoutBinding
import kotlinx.android.synthetic.main.how_to_play_layout.*

class HowToPlayActivity:AppCompatActivity()
{
    private lateinit var binding:HowToPlayLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= HowToPlayLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        how_to_play_screen_backButton.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this@HowToPlayActivity,
                    DiscoverScreenActivity::class.java
                ).putExtra("isFromLogin", "no")
            )

            finish()
        })
    }
}