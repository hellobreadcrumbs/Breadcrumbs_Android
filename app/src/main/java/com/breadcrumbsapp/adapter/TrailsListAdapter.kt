package com.breadcrumbsapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.interfaces.POIclickedListener
import com.breadcrumbsapp.model.DistanceMatrixApiModel
import com.breadcrumbsapp.model.GetEventsModel
import com.breadcrumbsapp.util.SessionHandlerClass
import com.bumptech.glide.Glide

internal class TrailsListAdapter(
    markers: List<GetEventsModel.Message>, private val poiListener: POIclickedListener,
    distance: ArrayList<DistanceMatrixApiModel>
) :
    RecyclerView.Adapter<TrailsListAdapter.MyViewHolder>() {

    private var localMarkers: List<GetEventsModel.Message> = markers
    var distanceObj: ArrayList<DistanceMatrixApiModel>? = distance
    private lateinit var context: Context
    private lateinit var sharedPreference:SessionHandlerClass

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var imageView: ImageView = view.findViewById(R.id.discoveredIcon)
        var trailsName: TextView = view.findViewById(R.id.trailsName)
        var distance: TextView = view.findViewById(R.id.tv_distance)
        var discoverStatus: TextView = view.findViewById(R.id.discoverStatus)
        var poiBackground: LinearLayoutCompat = view.findViewById(R.id.poiBackground)
        var mainLayout: FrameLayout = view.findViewById(R.id.list_adapter_constraintLayout)
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.trail_list_adapter, parent, false)
        context = parent.context
        sharedPreference= SessionHandlerClass(context)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //val bgImage = mResources[position]
        // holder.imageView.setImageResource(bgImage)


        try {

            println("localSize = ${localMarkers.size}")
            println("distanceObj = ${distanceObj!!.size}")


            holder.trailsName.text = localMarkers[position].title
            holder.distance.text = distanceObj!![position].distance
            println("mainLayout ${holder.trailsName.text}")

            holder.mainLayout.setOnClickListener {
                //println("mainLayout ${holder.trailsName.text}")
                poiListener.onClickedPOIItem(localMarkers[position].id)
                notifyDataSetChanged()
            }

            if (localMarkers[position].disc_id == null) {
                if (sharedPreference.getSession("selected_trail_id") == "4")
                {
                    Glide.with(context)
                        .load(R.drawable.list_poi_icon)
                        .into(holder.imageView)

                }
                else if (sharedPreference.getSession("selected_trail_id") == "6")
                {
                    Glide.with(context)
                        .load(R.drawable.hanse_trail_list_icon_undiscovered)
                        .into(holder.imageView)
                }
                holder.poiBackground.background =
                    context!!.resources.getDrawable(R.drawable.trail_banner_undiscovered)
                holder.discoverStatus.text = "Undiscovered"
            } else {
                if (sharedPreference.getSession("selected_trail_id") == "4")
                {
                    Glide.with(context)
                        .load(R.drawable.discovered_poi_ico_banner)
                        .into(holder.imageView)
                }
                else if (sharedPreference.getSession("selected_trail_id") == "6")
                {
                    Glide.with(context)
                        .load(R.drawable.hanse_trail_list_icon__discovered)
                        .into(holder.imageView)
                }


                holder.poiBackground.background =
                    context!!.resources.getDrawable(R.drawable.trail_banner_discovered)
                holder.discoverStatus.text = "Discovered"
            }

        } catch (e: Exception) {
            e.printStackTrace()

        }

    }

    override fun getItemCount(): Int {
        return localMarkers.size
    }
}