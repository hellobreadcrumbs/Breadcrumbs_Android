package com.breadcrumbsapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.model.DiscoverScreenModel

class POI_SearchAdapterBackUp(
    var list: List<DiscoverScreenModel.Message.Markers>
) : RecyclerView.Adapter<POI_SearchAdapterBackUp.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_list_adapter, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.name.text= list[position].name

        holder.mainLayout.setOnClickListener(View.OnClickListener {
            println("mainLayout ${holder.name.text}")
            notifyDataSetChanged()
        })
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder( item: View) : RecyclerView.ViewHolder(item) {
        var name: TextView =item.findViewById(R.id.trailsName)
        var mainLayout: FrameLayout =item.findViewById(R.id.poi_adapter_constraintLayout)
        // var age: TextView =item.findViewById(R.id.age)
    }
}