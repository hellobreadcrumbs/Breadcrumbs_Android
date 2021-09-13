package com.breadcrumbsapp.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.breadcrumbsapp.view.frag.NewFriendRequestFrag
import com.breadcrumbsapp.view.frag.PendingFriendRequestFrag

class FriendRequestViewPagerAdapter (activity: AppCompatActivity):
    FragmentStateAdapter(activity){
    private val fragmentList = ArrayList<Fragment>()
    private val fragmentTitleList = ArrayList<String>()
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {
                NewFriendRequestFrag.newInstance("","")
            }
            1 -> {
                PendingFriendRequestFrag.newInstance("","")
            }
            else ->{
                NewFriendRequestFrag.newInstance("","")
            }

        }
    }


}