package com.example.weatherapp.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.data.list.HourlyWeatherItem
import kotlinx.android.synthetic.main.hourly_weather_item.view.*

class HourlyWeatherAdapter(
    private val hourlyWeatherItem: List<HourlyWeatherItem>
) : RecyclerView.Adapter<HourlyWeatherAdapter.MyViewHolder>() {


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clockTextView: TextView = itemView.clockTextView
        val hourlyWeatherImageView: ImageView = itemView.hourlyWeatherImageView
        val disTextView: TextView = itemView.disTextView
        val tempTextView: TextView = itemView.tempTextView

    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HourlyWeatherAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.hourly_weather_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HourlyWeatherAdapter.MyViewHolder, position: Int) {
        val currentItem = hourlyWeatherItem[position]
        holder.clockTextView.text = currentItem.p0
        holder.hourlyWeatherImageView.setImageResource(currentItem.p1!!)
        holder.disTextView.text = currentItem.p2
        holder.tempTextView.text = currentItem.p3
    }

    override fun getItemCount() = hourlyWeatherItem.size
}