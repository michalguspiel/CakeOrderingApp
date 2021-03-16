package com.erdees.cakeorderingapp.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.viewmodel.CartItemsRecyclerAdapterViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.HashMap

class CartItemsRecyclerAdapter(val map: MutableMap<String, Long>,
                               val activity: Activity,
                               val viewModel: CartItemsRecyclerAdapterViewModel,
                               val viewLifecycleOwner: LifecycleOwner) :
    RecyclerView.Adapter<CartItemsRecyclerAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.cart_recycler_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        /**Access to firebase and user here*/
        val auth = Firebase.auth
        val db = Firebase.firestore
        /**Binders*/
        val image       = holder.itemView.findViewById<ImageView>(R.id.cart_recycler_item_image)
        val name        = holder.itemView.findViewById<TextView>(R.id.cart_recycler_item_name)
        val quantity    = holder.itemView.findViewById<TextView>(R.id.cart_recycler_item_quantity)
        val totalPrice  = holder.itemView.findViewById<TextView>(R.id.cart_recycler_item_total_price)


        /**I need to get model for each holder so...*/
        val productId  = map.keys.toList()[position]
        val amount     = map.values.toList()[position]


        db.collection("products").document(productId).get()
            .addOnSuccessListener {
                name.text =  it["productName"].toString()
                Glide.with(activity)
                    .load(it["productPictureUrl"])
                    .override(100, 100)
                    .centerCrop()
                    .into(image)
                val priceOfProduct = it["productPrice"].toString().toDouble() * amount
                totalPrice.text = "Total price: " + priceOfProduct.toString()
                viewModel.setPrice(priceOfProduct)

            }
        quantity.text = "Quantity: " + amount.toString()



    }

    override fun getItemCount(): Int {
        Log.i("CartAdapter",map.size.toString())
        return map.size
    }
}