package com.erdees.cakeorderingapp.adapter

import android.app.Activity
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.model.PresentedItem

class HorizontalAdapter (private val picList: List<String>, private val activity: Activity, private val screenWidth: Int)
    : RecyclerView.Adapter<HorizontalAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.store_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val eachLayout = LayoutInflater.from(activity).inflate(R.layout.horizontal_recycler_item,parent,false)
        return  ItemViewHolder(eachLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        setPicture(picList[position],holder.image)
    }

    override fun getItemCount(): Int {
        return picList.size
    }

    private fun setPicture(imageUrl: String, image: ImageView){
        Glide.with(activity)
            .load(imageUrl)
            .override(screenWidth, 300)
            .centerCrop()
            .into(image)
    }

}