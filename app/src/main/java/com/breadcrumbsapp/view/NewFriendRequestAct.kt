package com.breadcrumbsapp.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.breadcrumbsapp.R
import com.breadcrumbsapp.adapter.FriendRequestViewPagerAdapter
import com.breadcrumbsapp.util.SessionHandlerClass
import com.breadcrumbsapp.viewmodel.NewFriendRequestViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_new_friend_request.*

class NewFriendRequestAct : AppCompatActivity() {
    private val tabNames = ArrayList<String>()
    private lateinit var viewModel : NewFriendRequestViewModel
    private lateinit var sessionHandlerClass: SessionHandlerClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_friend_request)
        sessionHandlerClass= SessionHandlerClass(applicationContext)
        viewModel = ViewModelProvider(this).get(NewFriendRequestViewModel::class.java)
        viewModel.setApi(this)

        val adapterVp = FriendRequestViewPagerAdapter(this)
        nfr_pager.adapter = adapterVp

        tabNames.add(getString(R.string.new_requests))
        tabNames.add(getString(R.string.pending_requests))

        TabLayoutMediator(nfr_tab_layout, nfr_pager) { tab, position ->
            //To get the first name of doppelganger celebrities
            tab.text = tabNames[position]

        }.attach()
        viewModel.getFriend(sessionHandlerClass.getSession("login_id")!!)

        nfr_add_friend_btn.setOnClickListener {
            startActivity(Intent(this, SearchFriendsListAct::class.java))
        }

        nfr_backButton.setOnClickListener { finish() }

    }
}