package com.breadcrumbsapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R


internal class AvailableTrailsAdapter(context: Context):
    RecyclerView.Adapter<AvailableTrailsAdapter.MyViewHolder>() {



    lateinit var context: Context
    private var lastChecked: RadioButton? = null
    private var lastCheckedPos = 0


    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {


       // var radioBtn:RadioButton=view.findViewById(R.id.radioBtn)

        var radioButton:RadioButton=view.findViewById(R.id.radioBtn)


    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.available_trails_layout_adapter, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
      //  holder.radioBtn.isChecked = position == mSelectedItem


        holder.radioButton.tag = position

        if(position==0 && holder.radioButton.isChecked)
        {
            lastChecked=holder.radioButton
            lastCheckedPos=0
        }

        holder.radioButton.setOnClickListener(View.OnClickListener {

            val cb = it as RadioButton
            val clickedPos = (cb.tag as Int).toInt()

            if (cb.isChecked) {
                if (lastChecked != null) {
                    lastChecked!!.isChecked = false

                }
                lastChecked = cb
                lastCheckedPos = clickedPos
            } else lastChecked = null
        })

    }
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }
    override fun getItemCount(): Int {
        return 12
    }
}