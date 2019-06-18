package com.example.openweather.adaptor

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import com.example.openweather.R
import com.example.openweather.activities.ScrollingActivity
import com.example.openweather.data.AppDatabase
import com.example.openweather.data.City
import com.example.openweather.touch.CityTouchHelperCallback
import kotlinx.android.synthetic.main.city_row.view.*
import java.util.*

class CityAdaptor : RecyclerView.Adapter<CityAdaptor.ViewHolder>, CityTouchHelperCallback {

    var cityItems = mutableListOf<City>()

    private var context: Context

    private val clickListener: (City) -> Unit

    constructor(context: Context, listCity: List<City>, clickListener: (City) -> Unit) : super() {
        this.context = context
        cityItems.addAll(listCity)
        this.clickListener = clickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val cityRowView = LayoutInflater.from(viewGroup.context).inflate(
            R.layout.city_row, viewGroup, false
        )
        return ViewHolder(cityRowView)
    }

    override fun getItemCount(): Int {
        return cityItems.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val currentCity = cityItems.get(viewHolder.adapterPosition)
        viewHolder.bind(currentCity, clickListener)
        viewHolder.btnDelete.setOnClickListener {
            deleteCity(viewHolder.adapterPosition)
        }
    }

    fun addCity(city: City) {
        cityItems.add(0, city)
        notifyItemInserted(0)
    }

    fun updateCity(city: City, editIndex: Int) {
        cityItems.set(editIndex, city)
        notifyItemChanged(editIndex)
    }

    private fun deleteCity(deletePosition: Int) {
        Thread {
            AppDatabase.getInstance(context).cityDao().deleteCity(cityItems.get(deletePosition))
            (context as ScrollingActivity).runOnUiThread{
                cityItems.removeAt(deletePosition)
                notifyItemRemoved(deletePosition)
            }
        }.start()
    }

    fun deleteAll() {
        Thread {
            AppDatabase.getInstance(context).cityDao().deleteAll()
            (context as ScrollingActivity).runOnUiThread{
                cityItems = mutableListOf()
                notifyDataSetChanged()
            }
        }.start()
    }

    override fun onDismissed(position: Int) {
        deleteCity(position)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(cityItems, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var btnDelete = itemView.btnDelete
        fun bind(city: City, clickListener: (City) -> Unit) {
            itemView.tvCity.text = city.cityName
            itemView.setOnClickListener { clickListener(city) }
        }
    }

}