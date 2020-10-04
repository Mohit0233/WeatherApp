package com.example.weatherapp.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.data.list.DayWeatherItem
import kotlinx.android.synthetic.main.day_weather_item.view.*

class DayWeatherAdapter(
    private val dayWeatherItem: List<DayWeatherItem>
) : RecyclerView.Adapter<DayWeatherAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView:TextView = itemView.dateTextView
        val dayTextView:TextView = itemView.dayTextView
        val dayWeatherImageView:ImageView = itemView.dayWeatherImageView
        val disTextView:TextView = itemView.disTextView
        val tempTextView:TextView = itemView.tempTextView
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DayWeatherAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.day_weather_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DayWeatherAdapter.MyViewHolder, position: Int) {
        val currentItem = dayWeatherItem[position]
        holder.dateTextView.text = currentItem.p0
        holder.dayTextView.text = currentItem.p1
        holder.dayWeatherImageView.setImageResource(currentItem.p2!!)
        holder.disTextView.text = currentItem.p3
        holder.tempTextView.text = currentItem.p4

    }

    override fun getItemCount() = dayWeatherItem.size

}