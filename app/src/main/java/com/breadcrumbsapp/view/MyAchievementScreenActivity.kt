package com.breadcrumbsapp.view

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.adapter.MyAchievementsListAdapter
import com.breadcrumbsapp.databinding.MyAchievementsLayoutBinding
import com.breadcrumbsapp.util.CommonData
import com.breadcrumbsapp.util.CommonData.Companion.getUserAchievementsModel
import kotlinx.android.synthetic.main.my_achievements_layout.*

class MyAchievementScreenActivity : AppCompatActivity() {
    private lateinit var binding: MyAchievementsLayoutBinding
    private lateinit var myAchievementsListAdapter: MyAchievementsListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MyAchievementsLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myAchievements_List.layoutManager=LinearLayoutManager(applicationContext,RecyclerView.VERTICAL,false)
        myAchievementsListAdapter= MyAchievementsListAdapter(getUserAchievementsModel!!)
        myAchievements_List.adapter=myAchievementsListAdapter


        my_achievements_screen_backButton.setOnClickListener(View.OnClickListener {
            finish()
        })


    }
}