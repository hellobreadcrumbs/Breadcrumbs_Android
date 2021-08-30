package com.breadcrumbsapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.breadcrumbsapp.databinding.AchievementUnlockLayoutBinding

class AchievementUnLockActivity:AppCompatActivity()
{
    private lateinit var binding:AchievementUnlockLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= AchievementUnlockLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}