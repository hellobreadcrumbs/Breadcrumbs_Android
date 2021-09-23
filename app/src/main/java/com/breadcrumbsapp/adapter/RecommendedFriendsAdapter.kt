package com.breadcrumbsapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.model.RecommendedFriendsModel
import com.breadcrumbsapp.view.profile.FriendProfileScreenActivity
import com.bumptech.glide.Glide


internal class RecommendedFriendsAdapter() :
    RecyclerView.Adapter<RecommendedFriendsAdapter.MyViewHolder>(), Filterable  {

    private var getFeedsLocalObj = ArrayList<RecommendedFriendsModel.Message>()
    private var filteredList  = ArrayList<RecommendedFriendsModel.Message>()
    private lateinit var context: Context


    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var friendNameTv: TextView = view.findViewById(R.id.friendName)
        var friendLevelTv: TextView = view.findViewById(R.id.friend_level_name)
        var recommendedFriendAdapterProfile: ImageView = view.findViewById(R.id.recommendedFriendAdapterProfile)



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
        try {
            if(data.experience==null||data.experience=="null")
            {
                val expIntVal: Int = 250
                holder.friendLevelTv.text = calculateUserLevel(expIntVal)
            }
            else{
                val expIntVal: Int = Integer.parseInt(data.experience)
                holder.friendLevelTv.text = calculateUserLevel(expIntVal)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        //recommendedFriendAdapterProfile
        Glide.with(context).load("${context.getString(R.string.staging_url)}${data.profile_picture}")
            .placeholder(context.resources.getDrawable(R.drawable.com_facebook_profile_picture_blank_portrait, null))
            .into(holder.recommendedFriendAdapterProfile)

        holder.itemView.setOnClickListener(View.OnClickListener {
          //  Toast.makeText(context,data.username,Toast.LENGTH_SHORT).show()
            context.startActivity(
                Intent(context, FriendProfileScreenActivity::class.java)
                    .putExtra("username", data.username)
                    .putExtra("friend_id", data.id)
                    .putExtra("total_xp", data.experience)
                    .putExtra("profile_pic", data.profile_picture)
                    .putExtra("player_level",holder.friendLevelTv.text.toString())
            )
        })

    }
    private fun calculateUserLevel(exp: Int): String {
        var ranking: String = ""
        var level: Int = 0
        var base: Int = 0
        var nextLevel = 0
        when (exp) {
            in 0..999 -> { // 1000 thresh
                ranking = "Recruit"
                level = 1
                base = 1000
                nextLevel = 1000
            }
            in 1000..1999 -> { // 1000 thresh
                ranking = "Recruit"
                level = 2
                base = 1000
                nextLevel = 2000
            }
            in 2000..2999 -> { // 1000 thresh
                ranking = "Recruit"
                level = 3
                base = 2000
                nextLevel = 3000
            }
            in 3000..3999 -> { // 1000 thresh
                ranking = "Recruit"
                level = 4
                base = 3000
                nextLevel = 4000
            }
            in 4000..5999 -> { // 2000 thresh
                ranking = "Recruit"
                level = 5
                base = 4000
                nextLevel = 6000
            }
            in 6000..7999 -> { // 2000 thresh
                ranking = "Recruit"
                level = 6
                base = 6000
                nextLevel = 8000
            }
            in 8000..9999 -> { // 2000 thresh
                ranking = "Recruit"
                level = 7
                base = 8000
                nextLevel = 10000
            }
            in 10000..11999 -> { // 2000 thresh
                ranking = "Recruit"
                level = 8
                base = 10000
                nextLevel = 12000
            }
            in 12000..13999 -> { // 2000 thresh
                ranking = "Recruit"
                level = 9
                base = 12000
                nextLevel = 14000
            }
            in 14000..16999 -> { // 2000 thresh
                ranking = "Navigator"
                level = 10
                base = 14000
                nextLevel = 17000

            }
            in 17000..20499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 11
                base = 17000
                nextLevel = 20500

            }
            in 20500..24499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 12
                base = 20500
                nextLevel = 24500

            }
            in 24500..28499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 13
                base = 24500
                nextLevel = 28500

            }
            in 28500..33499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 14
                base = 28500
                nextLevel = 33500

            }
            in 33500..38999 -> { // 2000 thresh
                ranking = "Navigator"
                level = 15
                base = 33500
                nextLevel = 39000

            }
            in 39000..44999 -> { // 2000 thresh
                ranking = "Navigator"
                level = 16
                base = 39000
                nextLevel = 45000

            }
            in 45000..51499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 17
                base = 45000
                nextLevel = 51500

            }
            in 51500..58499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 18
                base = 51500
                nextLevel = 58500

            }
            in 58500..65999 -> { // 2000 thresh
                ranking = "Navigator"
                level = 19
                base = 58500
                nextLevel = 66000

            }
            in 66000..73999 -> { // 2000 thresh
                ranking = "Captain"
                level = 20
                base = 66000
                nextLevel = 74000

            }
        }

        return "$ranking LV. $level"


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