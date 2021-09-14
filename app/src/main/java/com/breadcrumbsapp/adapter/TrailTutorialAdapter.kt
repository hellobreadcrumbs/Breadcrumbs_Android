package com.breadcrumbsapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R

internal class TrailTutorialAdapter :
    RecyclerView.Adapter<TrailTutorialAdapter.MyViewHolder>() {
    private var mResources = intArrayOf(
        R.drawable.trail_tutorial_1,
        R.drawable.trail_tutorial_2,
        R.drawable.trail_tutorial_3,
        R.drawable.trail_tutorial_4
    )

    //trail_tutorial_4

    /*
    var mainContentString: Array<String> = arrayOf(
        "Select a trail to explore, you’ll never know what you’ll find!",
        "Discover all the locations on the map to earn experience points",
        "When you are close enough to a location, the \"Tap to Discover\" button will appear",
        "Fulfil specific tasks and stand a chance to win these rewards and more"
    )

    var subContentString: Array<String> = arrayOf(
        "Tap the Trail button on the top right to choose a trail that is right for you.",
        "The other coloured markers show you the other animals found in the Night Safari.",
        "You will be prompted to either scan a QR code at the station or open your camera using the buttons above.",
        "Go to the Rewards page or the Newsfeed page to find out more on how you can win!"
    )*/

    var mainContentString: Array<String> = arrayOf(
        "Select a trail to explore, you’ll never know what you might find!",
        "Discover all Points of Interest (POIs) to complete the trail",
        "When you're close enough to a location, hit \"Discover\"",
        "Fulfil specific tasks and stand a chance to win exclusive rewards"
    )
    var subContentString: Array<String> = arrayOf(
        "You can do this by tapping the Trail button on the top right to choose a trail that's right for you.",
        "Grey icons represent POIs you've yet to discover - go and explore!",
        "You'll be promoted to either scan a QR code at the station or open your camera with the buttons above.",
        "Go to Rewards to find out more about how you can win!"
    )


    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var imageView: ImageView = view.findViewById(R.id.trailImageView)
        var mainContent: TextView = view.findViewById(R.id.mainContent)
        var subContent: TextView = view.findViewById(R.id.subContent)

    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.trail_tutorial_adapter_new, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val bgImage = mResources[position]
        holder.imageView.setImageResource(bgImage)

        holder.mainContent.text = mainContentString[position]
        holder.subContent.text = subContentString[position]


    }

    override fun getItemCount(): Int {
        return mResources.size
    }
}