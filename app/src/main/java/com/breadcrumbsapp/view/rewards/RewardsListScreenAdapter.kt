package com.breadcrumbsapp.view.rewards

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.view.rewards.rewards_details.RewardsDetailsActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.rewards_screen_details_layout.*
import java.io.Serializable
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

internal class RewardsListScreenAdapter(
    val context: Context,
    private val rewardsList: List<GetRewardsDataModel.Message>,
    private val backgroundColor: Int
) :
    RecyclerView.Adapter<RewardsListScreenAdapter.MyViewHolder>() {

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var rewardsExpDate: TextView = view.findViewById(R.id.tv_rewards_exp_date)
        var rewardsPoint: TextView = view.findViewById(R.id.tx_rewards_point)
        var rewardsTitle: TextView = view.findViewById(R.id.tv_rewards_title)
        var imageViewRewardsIcon: ImageView = view.findViewById(R.id.iv_reward_icon)
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.rewards_screen_adapter, parent, false)
        itemView.background = ContextCompat.getDrawable(context, backgroundColor)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        rewardsList[position].apply {
           // holder.rewardsExpDate.text = enddate
            val originalFormat: DateFormat = SimpleDateFormat("yyyy-MM-DD HH:MM:SS", Locale.ENGLISH)  //2020-11-27 12:27:04
            val targetFormat: DateFormat = SimpleDateFormat("DD MMMM, yyyy")

            try
            {
                val postCreatedDate: Date = originalFormat.parse(enddate)
                val formattedDate: String = targetFormat.format(postCreatedDate)
                println("formattedDate $formattedDate")
                holder.rewardsExpDate.text = "Valid until $formattedDate"
            }
            catch (e: Exception)
            {
                e.printStackTrace()
                holder.rewardsExpDate.text = "Valid until $enddate"
            }
            with(holder.rewardsPoint) {
                visibility = View.GONE
                text = reward_id
            }
            holder.rewardsTitle.text = rewardtitle
            var imagePath=context.resources.getString(R.string.staging_url)+reward_img
            Glide.with(context).load(imagePath)
                .into(holder.imageViewRewardsIcon)
        }
        holder.itemView.setOnClickListener {
            with(context) {
                startActivity(
                    Intent(
                        this,
                        RewardsDetailsActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("rewardModelData", rewardsList[position])
                )
            }
        }
    }

    override fun getItemCount() = rewardsList.size
}