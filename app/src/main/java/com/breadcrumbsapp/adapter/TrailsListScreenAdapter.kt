package com.breadcrumbsapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R

internal class TrailsListScreenAdapter :
    RecyclerView.Adapter<TrailsListScreenAdapter.MyViewHolder>() {


    private var trailBanner = intArrayOf(
        R.drawable.pioneer_trail_banner,
        R.drawable.pioneer_trail_banner,
        R.drawable.anthology_trail_banner

    )

    private var trailIcons = intArrayOf(
        R.drawable.breadcrumbs_trail,
        R.drawable.wild_about_twlight_icon,
        R.drawable.anthology_trail_icon

    )

    private var trailNameString: Array<String> = arrayOf("PIONEER TRAIL","WILD ABOUT TWILIGHT TRAIL","ANTHOLOGY TRAIL")


    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var trailImage: ImageView = view.findViewById(R.id.trailImage)
        var trailName: TextView = view.findViewById(R.id.trails_screen_adapter_poi_name)
        var trailIcon: ImageView = view.findViewById(R.id.trails_screen_adapter_trailIcon)

    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.trails_screen_adapter, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.trailImage.setImageResource(trailBanner[position])
        holder.trailIcon.setImageResource(trailIcons[position])
        holder.trailName.text=trailNameString[position]





    }

    override fun getItemCount(): Int {
        return trailBanner.size
    }
}