package com.breadcrumbsapp.view.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.breadcrumbsapp.databinding.UserProfileScreenLayoutBinding
import com.breadcrumbsapp.view.MyFriendsListScreenActivity
import kotlinx.android.synthetic.main.user_profile_screen_layout.*

class ProfileScreenActivity:AppCompatActivity()
{
    private lateinit var binding:UserProfileScreenLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= UserProfileScreenLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        edit_iv.setOnClickListener(View.OnClickListener {
            startActivity(Intent(applicationContext,ProfileEditActivity::class.java).putExtra("from","activity"))
        })

        profile_screen_backButton.setOnClickListener(View.OnClickListener {
            finish()
        })

        my_friends_list_iv.setOnClickListener(View.OnClickListener {
            startActivity(Intent(applicationContext,MyFriendsListScreenActivity::class.java))
        })
    }
}