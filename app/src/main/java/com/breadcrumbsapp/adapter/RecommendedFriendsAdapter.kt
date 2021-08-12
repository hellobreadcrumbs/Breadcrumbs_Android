package com.breadcrumbsapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.borjabravo.readmoretextview.ReadMoreTextView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.model.RecommendedFriendsModel
import com.mikhaellopez.circularimageview.CircularImageView


internal class RecommendedFriendsAdapter(getFeed: List<RecommendedFriendsModel.Message>) :
    RecyclerView.Adapter<RecommendedFriendsAdapter.MyViewHolder>() {

    private var getFeedsLocalObj: List<RecommendedFriendsModel.Message> = getFeed
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


        val data = getFeedsLocalObj[position]

        holder.friendNameTv.text=data.username


    }


    override fun getItemCount(): Int {
        return getFeedsLocalObj.size
    }
}