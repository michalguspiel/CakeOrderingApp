package androidx.recyclerview.widget.com.erdees.cakeorderingapp.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.formatNumber
import com.erdees.cakeorderingapp.model.UserShoppingCart


class EachOrderCartItemsAdapter(
    val itemList: List<UserShoppingCart>,
    val activity: Activity
) : RecyclerView.Adapter<EachOrderCartItemsAdapter.ItemViewHolder>() {
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.each_order_recycler_item,parent,false)
        return ItemViewHolder(view)
    }

    private fun setPicture(imageUrl : String, image: ImageView){
        Glide.with(activity)
            .load(imageUrl)
            .override(100, 100)
            .centerCrop()
            .into(image)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val image = holder.itemView.findViewById<ImageView>(R.id.each_order_recycler_item_image)
        val name = holder.itemView.findViewById<TextView>(R.id.each_order_recycler_item_name)
        val quantity = holder.itemView.findViewById<TextView>(R.id.each_order_recycler_item_quantity)
        val totalPrice = holder.itemView.findViewById<TextView>(R.id.each_order_recycler_item_total_price)

        name.text = itemList[position].productName
        quantity.text ="Quantity: " + itemList[position].quantity.toString()
        val totalPriceOfProduct = itemList[position].productPrice * itemList[position].quantity
        totalPrice.text ="Total price: " +  formatNumber(totalPriceOfProduct)
        setPicture(itemList[position].productPictureUrl,image)

    }

    override fun getItemCount(): Int {
       return  itemList.size
    }

}