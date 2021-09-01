package com.breadcrumbsapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.interfaces.POIclickedListener
import com.breadcrumbsapp.model.DistanceMatrixApiModel

import com.breadcrumbsapp.model.GetEventsModel
import com.breadcrumbsapp.util.SessionHandlerClass
import com.bumptech.glide.Glide

class POI_SearchAdapter(
    var list: List<GetEventsModel.Message>, private val poiListener : POIclickedListener, var distanceObj: ArrayList<DistanceMatrixApiModel>
) : RecyclerView.Adapter<POI_SearchAdapter.ViewHolder>() {

    private lateinit var context: Context
    private lateinit var sessionHandlerClass: SessionHandlerClass
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_list_adapter, parent, false)
        context=parent.context
        sessionHandlerClass= SessionHandlerClass(context)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.distanceTv.text=distanceObj[position].distance
        holder.name.text= list[position].title

        holder.mainLayout.setOnClickListener {
            println("mainLayout ${holder.name.text}")
            poiListener.onClickedPOIItem(list[position].id)
            notifyDataSetChanged()
        }


        if (list[position].disc_id == null) {
            if (sessionHandlerClass.getSession("selected_trail_id") == "4")
            {
                Glide.with(context)
                    .load(R.drawable.list_poi_icon)
                    .into(holder.imageView)

            }
            else if (sessionHandlerClass.getSession("selected_trail_id") == "6")
            {
                Glide.with(context)
                    .load(R.drawable.hanse_trail_list_icon_undiscovered)
                    .into(holder.imageView)
            }
            holder.searchPoiBackground.background =
                context!!.resources.getDrawable(R.drawable.trail_banner_undiscovered)
            holder.searchDiscoverStatus.text = "Undiscovered"
        } else {
            if (sessionHandlerClass.getSession("selected_trail_id") == "4")
            {
                Glide.with(context)
                    .load(R.drawable.discovered_poi_ico_banner)
                    .into(holder.imageView)
            }
            else if (sessionHandlerClass.getSession("selected_trail_id") == "6")
            {
                Glide.with(context)
                    .load(R.drawable.hanse_trail_list_icon__discovered)
                    .into(holder.imageView)
            }

            holder.searchPoiBackground.background =
                context!!.resources.getDrawable(R.drawable.trail_banner_discovered)
            holder.searchDiscoverStatus.text = "Discovered"
        }

    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder( item: View) : RecyclerView.ViewHolder(item) {
        var name: TextView =item.findViewById(R.id.trailsName)
        var mainLayout: FrameLayout =item.findViewById(R.id.poi_adapter_constraintLayout)
        var distanceTv:TextView=item.findViewById(R.id.tv_distance)
        var imageView:ImageView=item.findViewById(R.id.discoveredIcon)
        var searchPoiBackground:LinearLayoutCompat=item.findViewById(R.id.searchPoiBackground)
        var searchDiscoverStatus:TextView=item.findViewById(R.id.searchDiscoverStatus)

    }
}