package com.erdees.cakeorderingapp.adapter

import android.app.Activity
import android.media.Image
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ColorStateListInflaterCompat.inflate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.model.PresentedItem
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.firestore.DocumentSnapshot


private const val normalItem = 0
private const val storePresentation = 1
private const val smthnElse = 2

class MainActivityRecyclerAdapter(
    private val activity: Activity,
    private val options: FirestorePagingOptions<PresentedItem>, private val screenWidth: Int
) :
    FirestorePagingAdapter<PresentedItem, MainActivityRecyclerAdapter.PresentedItemViewHolder>(
        options
    ) {

    class PresentedItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(presentedItem: PresentedItem) {}
    }

    class StartingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(presentedItem: PresentedItem) {}

    }

    override fun onBindViewHolder(
        holder: PresentedItemViewHolder,
        position: Int,
        model: PresentedItem
    ) {
        when (getItemViewType(position)) {
            normalItem -> {
                val image = holder.itemView.findViewById<ImageView>(R.id.product_image)
                val name = holder.itemView.findViewById<TextView>(R.id.product_name)
                val desc = holder.itemView.findViewById<TextView>(R.id.product_desc)
                name.text = model.name
                desc.text = model.description
                Glide.with(activity)
                    .load(model.pictureUrl)
                    .override(screenWidth, 300)
                    .centerCrop()
                    .into(image)
            }
            storePresentation -> {
                  val recyclerView = holder.itemView.findViewById<RecyclerView>(R.id.horizontal_recycler)
                      recyclerView.adapter = HorizontalAdapter(model.picturesArray,activity,screenWidth)
                val linearLayoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL, false)
                recyclerView.layoutManager = linearLayoutManager

            }

            }
        }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position)?.get("type") as Long == 0L) normalItem
        else storePresentation
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PresentedItemViewHolder {
        if (viewType == normalItem) {
            val view = LayoutInflater.from(activity)
                .inflate(R.layout.main_recycler_item, parent, false)
            return PresentedItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(activity)
                .inflate(R.layout.horizontal_recycler_view, parent, false)
            return PresentedItemViewHolder(view)
        }

    }
}




