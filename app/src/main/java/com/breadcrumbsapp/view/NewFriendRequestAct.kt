package com.breadcrumbsapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.breadcrumbsapp.R
import com.breadcrumbsapp.adapter.FriendRequestViewPagerAdapter
import com.breadcrumbsapp.databinding.ActivityNewFriendRequestBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_new_friend_request.*

class NewFriendRequestAct : AppCompatActivity() {
    private lateinit var binding:ActivityNewFriendRequestBinding
    val tab_names = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityNewFriendRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter_vp = FriendRequestViewPagerAdapter(this)
        nfr_pager.adapter = adapter_vp

        tab_names.add(getString(R.string.new_requests))
        tab_names.add(getString(R.string.pending_requests))

        TabLayoutMediator(nfr_tab_layout, nfr_pager) { tab, position ->
            //To get the first name of doppelganger celebrities
            tab.text = tab_names[position]
        }.attach()


    }
}