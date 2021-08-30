package com.breadcrumbsapp.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.breadcrumbsapp.R
import com.breadcrumbsapp.adapter.FriendRequestViewPagerAdapter
import com.breadcrumbsapp.viewmodel.NewFriendRequestViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_new_friend_request.*

class NewFriendRequestAct : AppCompatActivity() {
    val tab_names = ArrayList<String>()
    lateinit var viewModel : NewFriendRequestViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_friend_request)
        viewModel = ViewModelProvider(this).get(NewFriendRequestViewModel::class.java)
        viewModel.setApi(this)

        val adapter_vp = FriendRequestViewPagerAdapter(this)
        nfr_pager.adapter = adapter_vp

        tab_names.add(getString(R.string.new_requests))
        tab_names.add(getString(R.string.pending_requests))

        TabLayoutMediator(nfr_tab_layout, nfr_pager) { tab, position ->
            //To get the first name of doppelganger celebrities
            tab.text = tab_names[position]

        }.attach()
        viewModel.getFriend("4700")

        nfr_add_friend_btn.setOnClickListener {
            startActivity(Intent(this, SearchFriendsListAct::class.java))
        }

        nfr_backButton.setOnClickListener(View.OnClickListener { finish() })

    }
}