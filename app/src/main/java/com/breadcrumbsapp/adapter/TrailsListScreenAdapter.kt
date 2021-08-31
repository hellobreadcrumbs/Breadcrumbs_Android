package com.breadcrumbsapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.model.GetTrailsModel
import com.breadcrumbsapp.view.TrailsDetailsActivity
import com.bumptech.glide.Glide

internal class TrailsListScreenAdapter(getTrails: List<GetTrailsModel.Message>) :
    RecyclerView.Adapter<TrailsListScreenAdapter.MyViewHolder>() {

    private var getTrailsList: List<GetTrailsModel.Message> = getTrails
    private lateinit var context: Context

    private var trailBanner = intArrayOf(
        R.drawable.pioneer_trail_banner,
        R.drawable.wild_twilight_trail_banner,
        R.drawable.anthology_trail_banner
    )

    private var trailIcons = intArrayOf(
        R.drawable.breadcrumbs_trail,
        R.drawable.wild_about_twlight_icon,
        R.drawable.anthology_trail_icon
    )

    private var trailNameString: Array<String> =
        arrayOf("PIONEER TRAIL", "WILD ABOUT TWILIGHT TRAIL", "ANTHOLOGY TRAIL")


    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var trailImage: ImageView = view.findViewById(R.id.trailImage)
        var trailName: TextView = view.findViewById(R.id.trails_screen_adapter_poi_name)
        var trailIcon: ImageView = view.findViewById(R.id.trails_screen_adapter_trailIcon)
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.trails_screen_adapter, parent, false)
        context = parent.context
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        /* holder.trailImage.setImageResource(trailBanner[position])
        holder.trailIcon.setImageResource(trailIcons[position])
        holder.trailName.text=trailNameString[position]  */


        //   holder.trailImage.setImageResource(getFeedsLocalObj[position].)
        //   holder.trailIcon.setImageResource(getFeedsLocalObj[position])


        val localImageUri =
            context.resources.getString(R.string.staging_url) + getTrailsList[position].banner_url
        println("localImageUri $localImageUri")
        Glide.with(context).load(localImageUri).into(holder.trailImage)
        holder.trailName.text = getTrailsList[position].name
        if (getTrailsList[position].name == "Wild About Twilight") {
            Glide.with(context).load(trailIcons[1]).into(holder.trailIcon)
        }
        else
        {
            Glide.with(context).load(trailIcons[2]).into(holder.trailIcon)
        }


        holder.itemView.setOnClickListener {
            with(context) {
                startActivity(
                    Intent(
                        context,
                        TrailsDetailsActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("position", position)
                        .putExtra("getTrailsListData", getTrailsList[position])
                )
            }
        }


    }

    override fun getItemCount(): Int {
        return getTrailsList.size
    }
}