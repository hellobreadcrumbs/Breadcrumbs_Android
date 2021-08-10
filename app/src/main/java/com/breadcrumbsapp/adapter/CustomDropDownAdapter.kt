package com.breadcrumbsapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.breadcrumbsapp.R

//https://www.tutorialsbuzz.com/2019/09/android-kotlin-custom-spinner-image-text.html
class CustomDropDownAdapter(val context: Context) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var trailIcons = intArrayOf(
        R.drawable.breadcrumbs_trail,
        R.drawable.wild_about_twlight_icon,
        R.drawable.anthology_trail_icon

    )
    private var trailNameString: Array<String> = arrayOf("PIONEER TRAIL","WILD ABOUT TWILIGHT TRAIL","ANTHOLOGY TRAIL")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View
        val vh: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.leaderboard_spinner_item, parent, false)
            vh = ItemHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemHolder
        }
        vh.label.text = trailNameString[position]

     //   val id = context.resources.getIdentifier(dataSource.get(position).url, "drawable", context.packageName)
     //   vh.img.setBackgroundResource(id)

        return view
    }

    override fun getItem(position: Int): Any? {
        return trailIcons[position]
    }

    override fun getCount(): Int {
        return trailIcons.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    private class ItemHolder(row: View?) {
        val label: TextView = row?.findViewById(R.id.text) as TextView
        val img: ImageView = row?.findViewById(R.id.img) as ImageView

    }

}