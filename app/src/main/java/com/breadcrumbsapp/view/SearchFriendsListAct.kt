package com.breadcrumbsapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.adapter.RecommendedFriendsAdapter
import com.breadcrumbsapp.databinding.ActivitySearchFriendsListBinding
import com.breadcrumbsapp.util.SessionHandlerClass
import com.breadcrumbsapp.viewmodel.SearchFriendsViewModel
import kotlinx.android.synthetic.main.activity_search_friends_list.*

class SearchFriendsListAct : AppCompatActivity() {
    lateinit var dataBinding : ActivitySearchFriendsListBinding
    lateinit var viewModel : SearchFriendsViewModel
    private lateinit var friendAdapter : RecommendedFriendsAdapter
    var friendList = ArrayList<com.breadcrumbsapp.model.RecommendedFriendsModel.Message>()
    private lateinit var sessionHandlerClass: SessionHandlerClass
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = ActivitySearchFriendsListBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)
        sessionHandlerClass= SessionHandlerClass(applicationContext)

        viewModel = ViewModelProvider(this).get(SearchFriendsViewModel::class.java)
        viewModel.setApi(this)

        friendAdapter = RecommendedFriendsAdapter()
        dataBinding.sfRv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        dataBinding.sfRv.adapter = friendAdapter

        viewModel.getFriends(sessionHandlerClass.getSession("login_id")!!)
        viewModel.mContext = this
        viewModel.friendList.observe(this, Observer {
            if (it != null){
                friendList.clear()
                friendList.addAll(it)
                friendAdapter.addList(friendList)

            }else{
                friendList.clear()
                friendAdapter.addList(friendList)
            }
        })

        sf_search_et.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (s.isNotEmpty()){
                        sf_search_img.setImageResource(R.drawable.places_ic_clear)
                        friendAdapter.filter.filter( s.toString())
                    }else{
                        friendAdapter.filter.filter( "")
                        sf_search_img.setImageResource(R.drawable.searchbar_icon)
                    }
                }
            }

        })

        sf_search_img.setOnClickListener {
            sf_search_et.text.clear()
            friendAdapter.filter.filter( "")

        }

        sf_backButton.setOnClickListener {
            onBackPressed()
        }

    }

}