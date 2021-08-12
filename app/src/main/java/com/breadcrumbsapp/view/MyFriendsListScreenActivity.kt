package com.breadcrumbsapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.breadcrumbsapp.databinding.FriendsListLayoutBinding

class MyFriendsListScreenActivity:AppCompatActivity()
{
    private lateinit var binding:FriendsListLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= FriendsListLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}