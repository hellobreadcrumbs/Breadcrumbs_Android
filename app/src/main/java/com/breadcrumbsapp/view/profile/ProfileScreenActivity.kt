package com.breadcrumbsapp.view.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.breadcrumbsapp.R
import com.breadcrumbsapp.databinding.UserProfileScreenLayoutBinding
import com.breadcrumbsapp.util.SessionHandlerClass
import com.breadcrumbsapp.view.MyFriendsListScreenActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.feed_layout.*
import kotlinx.android.synthetic.main.profile_edit_layout.*
import kotlinx.android.synthetic.main.user_profile_screen_layout.*
import kotlinx.android.synthetic.main.user_profile_screen_layout.profile_edit_screen_profile_pic_iv

class ProfileScreenActivity:AppCompatActivity()
{
    private lateinit var binding:UserProfileScreenLayoutBinding
    private lateinit var sessionHandlerClass: SessionHandlerClass
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= UserProfileScreenLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionHandlerClass= SessionHandlerClass(applicationContext)
        edit_iv.setOnClickListener(View.OnClickListener {
            startActivity(Intent(applicationContext,ProfileEditActivity::class.java).putExtra("from","activity"))
        })

        if(sessionHandlerClass.getSession("player_photo_url")!=null && sessionHandlerClass.getSession("player_photo_url")!="")
        {
            Glide.with(applicationContext).load(sessionHandlerClass.getSession("player_photo_url")).into(profile_edit_screen_profile_pic_iv)
        }
        else
        {
            Glide.with(applicationContext).load(R.drawable.no_image).into(profile_edit_screen_profile_pic_iv)
        }
        user_profile_screen_backButton.setOnClickListener(View.OnClickListener {
            finish()
        })

        my_friends_list_iv.setOnClickListener(View.OnClickListener {
            startActivity(Intent(applicationContext,MyFriendsListScreenActivity::class.java))
        })
    }
}