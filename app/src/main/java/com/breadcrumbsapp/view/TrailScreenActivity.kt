package com.breadcrumbsapp.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.adapter.TrailsListScreenAdapter
import com.breadcrumbsapp.databinding.TrailsScreenLayoutBinding
import kotlinx.android.synthetic.main.trails_screen_layout.*

class TrailScreenActivity:AppCompatActivity()
{
    private lateinit var binding:TrailsScreenLayoutBinding
    private lateinit var trailsListScreenAdapter:TrailsListScreenAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= TrailsScreenLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)


        trailsListScreenAdapter = TrailsListScreenAdapter()
        trailsScreenRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        trailsScreenRecyclerView.adapter = trailsListScreenAdapter

        trails_screen_back_button.setOnClickListener(View.OnClickListener {
            finish()
        })
    }
}