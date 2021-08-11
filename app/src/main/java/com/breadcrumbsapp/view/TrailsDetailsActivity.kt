package com.breadcrumbsapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.breadcrumbsapp.databinding.TrailDetailsLayoutBinding

class TrailsDetailsActivity:AppCompatActivity()
{
    private lateinit var binding:TrailDetailsLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= TrailDetailsLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}