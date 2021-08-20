package com.breadcrumbsapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.model.GetFriendsListModel
import com.breadcrumbsapp.model.RecommendedFriendsModel

internal class GetFriendListAdapter(getFriendsListModel: List<GetFriendsListModel.Message>) :
    RecyclerView.Adapter<GetFriendListAdapter.MyViewHolder>()
{

    private var getFriendsListModelLocalObj: List<GetFriendsListModel.Message> = getFriendsListModel
    private lateinit var context: Context


    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var friendNameTv: TextView = view.findViewById(R.id.friendName)



    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.friend_list_adapter, parent, false)
        context = parent.context
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        val data = getFriendsListModelLocalObj[position]

        holder.friendNameTv.text=data.username


    }


    override fun getItemCount(): Int {
        return getFriendsListModelLocalObj.size
    }
}