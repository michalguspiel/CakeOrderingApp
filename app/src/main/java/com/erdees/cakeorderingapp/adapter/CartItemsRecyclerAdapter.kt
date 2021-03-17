package com.erdees.cakeorderingapp.adapter

import android.app.Activity
import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.fragments.MyCartFragment
import com.erdees.cakeorderingapp.viewmodel.CartItemsRecyclerAdapterViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.*


class CartItemsRecyclerAdapter(
    val map: MutableMap<String, Long>,
    val activity: Activity,
    val viewModel: CartItemsRecyclerAdapterViewModel,
    val viewLifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<CartItemsRecyclerAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    /**Access to firebase and user here*/
    val auth = Firebase.auth
    val db = Firebase.firestore
    val docRef = db.collection("userShoppingCart").document(auth.uid!!)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.cart_recycler_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {


        /**Binders*/
        val image = holder.itemView.findViewById<ImageView>(R.id.cart_recycler_item_image)
        val name = holder.itemView.findViewById<TextView>(R.id.cart_recycler_item_name)
        val quantity = holder.itemView.findViewById<TextView>(R.id.cart_recycler_item_quantity)
        val totalPrice = holder.itemView.findViewById<TextView>(R.id.cart_recycler_item_total_price)
        val editButton = holder.itemView.findViewById<ImageButton>(R.id.cart_recycler_item_button)


        /**I need to get model for each holder so...*/
        val productId = map.keys.toList()[position]
        val amount = map.values.toList()[position]



        db.collection("products").document(productId).get()
            .addOnSuccessListener {
                name.text = it["productName"].toString()
                Glide.with(activity)
                    .load(it["productPictureUrl"])
                    .override(130, 130)
                    .centerCrop()
                    .into(image)
                val priceOfProduct = it["productPrice"].toString().toDouble() * amount
                totalPrice.text = "Total price: " + NumberFormat.getCurrencyInstance(Locale.FRANCE).format(priceOfProduct) + "."
                viewModel.setPrice(priceOfProduct)

            }
        quantity.text = "Quantity: " + amount.toString()


        editButton.setOnClickListener {
            val numberPicker = NumberPicker(activity)
            numberPicker.minValue = 1
            numberPicker.maxValue = 999
            val dialog = AlertDialog.Builder(activity)
                .setTitle("Change quantity")
                .setView(numberPicker)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Submit", null)
                .setNeutralButton("Delete item", null)
                .show()
            numberPicker.value = amount.toInt()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val data = hashMapOf(productId to numberPicker.value.toLong())
                db.collection("userShoppingCart")
                    .document(auth.uid!!).set(data, SetOptions.merge())
                dialog.dismiss()
            }
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                val updates = hashMapOf<String, Any>(
                    productId to FieldValue.delete()
                )
                docRef.update(updates).addOnCompleteListener { }
                dialog.dismiss()
            }


        }


    }

    override fun getItemCount(): Int {
        Log.i("CartAdapter", map.size.toString())
        return map.size
    }

}