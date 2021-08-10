package com.breadcrumbsapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.view.TutorialActivity

internal class TutorialScreenAdapter(private val activity: TutorialActivity):
    RecyclerView.Adapter<TutorialScreenAdapter.MyViewHolder>() {
    private var mResources = intArrayOf(
        R.drawable.tut_new_1,
        R.drawable.tut_2,
        R.drawable.tut_new_3,
        R.drawable.tut_4
    )

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var imageView: ImageView = view.findViewById(R.id.imageView)

    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.tutorial_adapter, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val bgImage = mResources[position]
        holder.imageView.setImageResource(bgImage)



    }

    override fun getItemCount(): Int {
        return mResources.size
    }
}