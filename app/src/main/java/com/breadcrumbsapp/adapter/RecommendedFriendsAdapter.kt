package com.breadcrumbsapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.model.RecommendedFriendsModel
import com.breadcrumbsapp.view.profile.FriendProfileScreenActivity


internal class RecommendedFriendsAdapter() :
    RecyclerView.Adapter<RecommendedFriendsAdapter.MyViewHolder>(), Filterable  {

    private var getFeedsLocalObj = ArrayList<RecommendedFriendsModel.Message>()
    private var filteredList  = ArrayList<RecommendedFriendsModel.Message>()
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


        val data = filteredList[position]

        holder.friendNameTv.text=data.username


        holder.itemView.setOnClickListener(View.OnClickListener {
          //  Toast.makeText(context,data.username,Toast.LENGTH_SHORT).show()
            context.startActivity(
                Intent(context, FriendProfileScreenActivity::class.java)
                    .putExtra("username", data.username)
                    .putExtra("friend_id", data.id)
                    .putExtra("total_xp", data.experience)
                    .putExtra("profile_pic", data.profile_picture)
                    .putExtra("player_level",holder.friendNameTv.text.toString())
            )
        })

    }


    override fun getItemCount(): Int {
        return filteredList.size
    }

    fun addList(list : List<RecommendedFriendsModel.Message>){
        getFeedsLocalObj.clear()
        getFeedsLocalObj.addAll(list)

        filteredList.clear()
        filteredList.addAll(list)
        notifyDataSetChanged()

    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var searchTxt = constraint.toString()?:""
                if (searchTxt.isNotEmpty()){
                    var list = ArrayList<RecommendedFriendsModel.Message>()

                    getFeedsLocalObj.filter {
                        (it.username.contains(searchTxt)) or (it.email.contains(searchTxt))
                    }.forEach {
                        list.add(it)
                    }
                    filteredList.clear()
                    filteredList.addAll(list)

                }else{
                    filteredList.clear()
                    filteredList.addAll(getFeedsLocalObj)
                }
                return FilterResults().apply { values = filteredList }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                filteredList = if (results?.values == null){
                    ArrayList<RecommendedFriendsModel.Message>()
                } else{
                    results?.values as ArrayList<RecommendedFriendsModel.Message>
                }
                notifyDataSetChanged()


            }

        }
    }
}