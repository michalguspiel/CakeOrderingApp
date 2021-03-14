package com.erdees.cakeorderingapp.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.model.Products
import java.text.NumberFormat
import java.util.*

/**This is recycler view which is presenting each product
 * starts from header shows name, price, taste variants, ingredients etc.
 * */

class EachProductAdapter(val model: Products, val activity: Activity) :
    RecyclerView.Adapter<EachProductAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        when (viewType) {


            0 -> {
                val view = LayoutInflater.from(activity)
                    .inflate(R.layout.each_product_picture_item, parent, false)
                return ItemViewHolder(view)
            }
            1 -> {
                val view = LayoutInflater.from(activity)
                    .inflate(R.layout.each_product_desc_item,parent,false)
                return ItemViewHolder(view)
            }
            2 -> {
                val view = LayoutInflater.from(activity)
                    .inflate(R.layout.each_product_price_and_buy_item,parent,false)
                return ItemViewHolder(view)

            }
            else -> {
                val view = LayoutInflater.from(activity)
                    .inflate(R.layout.each_product_picture_item, parent, false)
                return ItemViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
when (getItemViewType(position)) {

    0 -> {  val picture = holder.itemView.findViewById<ImageView>(R.id.each_product_picture)
        Glide.with(activity)
            .load(model.productPictureUrl)
            .centerCrop()
            .into(picture)
    }
    1 -> {
        val name = holder.itemView.findViewById<TextView>(R.id.each_product_name)
        val description = holder.itemView.findViewById<TextView>(R.id.each_product_desc)
        name.text = model.productName
        description.text = model.productDesc
    }
    2 -> {
        val price = holder.itemView.findViewById<TextView>(R.id.each_product_price)
        val formatPrice =  "%.2f€".format(model.productPrice)
        price.text = formatPrice

        val addToCartButton = holder.itemView.findViewById<Button>(R.id.each_product_add_to_cart_button)
        addToCartButton.setOnClickListener {
            TODO() // Add this product to cart OR open dialog and let pick quantity and date?
        }

    }
    else -> {}
}
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun getItemViewType(position: Int): Int {
        return position // each position is different viewType which makes each onCreateViewHolder and onBindViewHolder different
    }
}
