package com.breadcrumbsapp.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.model.DiscoverScreenModel.Message.Markers


class AutoCompletePlaceAdapter(context: Context, placesList: List<Markers>) :
    ArrayAdapter<Markers>(context, 0, placesList) {
    private val allPlacesList: List<Markers> = placesList


    private val tempItems: ArrayList<Markers> = ArrayList<Markers>(allPlacesList)
    private val suggestions: ArrayList<Markers> = ArrayList<Markers>()
    override fun getFilter(): Filter {
        return placeFilter
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView: View? = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                R.layout.search_list_adapter, parent, false
            )
        }

        val trailsName: TextView = convertView!!.findViewById(R.id.trailsName)
        val place = getItem(position)
        if (place != null) {
            trailsName.text = place.name
            //Glide.with(convertView!!).load(place.getImageUrl()).into<Target<Drawable>>(placeImage)
        }
        return convertView
    }

    private val placeFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val queryString = constraint?.toString()?.toLowerCase()
            if (queryString != null) {
                println("queryString IF = $queryString")

                for (markers in tempItems!!) {
                    if (markers.name.toLowerCase()
                            .contains(constraint.toString().toLowerCase())
                    ) {
                        suggestions!!.add(markers)
                    }
                }

                val filterResults = FilterResults()
                filterResults.values = suggestions
                filterResults.count = suggestions!!.size
                return filterResults

            } else {
                println("queryString = $queryString")
                return FilterResults()
            }


        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {

            if(results.values!=null)
            {
                clear()
                println("result = ${results.count}")
                val filterList: List<Markers> = results.values as List<Markers>
                if (results.count > 0) {
                    clear()
                    for (marker in filterList) {
                        add(marker)
                        notifyDataSetChanged()
                    }
                }
            }


        }

        override fun convertResultToString(resultValue: Any): CharSequence {
            return (resultValue as Markers?)!!.name
        }
    }

}