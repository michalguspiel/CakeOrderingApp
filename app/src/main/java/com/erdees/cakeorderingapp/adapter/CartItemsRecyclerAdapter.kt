package com.erdees.cakeorderingapp.adapter

import android.app.Activity
import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.model.UserShoppingCart
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.*


class CartItemsRecyclerAdapter(
    val options: FirestoreRecyclerOptions<UserShoppingCart>,
    val activity: Activity
) :
    FirestoreRecyclerAdapter<UserShoppingCart, CartItemsRecyclerAdapter.ItemViewHolder>(options) {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    /**Access to firebase and user here*/
    val auth = Firebase.auth
    val db = Firebase.firestore
    val docRef = db.collection("userShoppingCart").document(auth.uid!!)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.cart_recycler_item, parent, false)
        return ItemViewHolder(view)
    }


    fun updateQuantity(newQuantity: Long, model: UserShoppingCart) {
        val ref = db.collection("userShoppingCart").document(model.docId)
        val newDocument = UserShoppingCart(
            model.docId,
            model.productId,
            model.userId,
            model.productName,
            model.productPrice,
            newQuantity,
            model.productPictureUrl
        )
        val newDocAsMap = newDocument.toMap()
        ref.update(newDocAsMap)
    }



    override fun onBindViewHolder(holder: ItemViewHolder, position: Int, model: UserShoppingCart) {
        /**Binders*/
        val image = holder.itemView.findViewById<ImageView>(R.id.cart_recycler_item_image)
        val name = holder.itemView.findViewById<TextView>(R.id.cart_recycler_item_name)
        val quantity = holder.itemView.findViewById<TextView>(R.id.cart_recycler_item_quantity)
        val totalPrice = holder.itemView.findViewById<TextView>(R.id.cart_recycler_item_total_price)
        val editButton = holder.itemView.findViewById<ImageButton>(R.id.cart_recycler_item_button)
        Glide.with(activity)
            .load(model.productPictureUrl)
            .override(130, 130)
            .centerCrop()
            .into(image)

        name.text = model.productName
        quantity.text = model.quantity.toString()
        val totalPriceOfProduct = model.productPrice * model.quantity
        totalPrice.text = "Total price: " + NumberFormat.getCurrencyInstance(Locale.FRANCE)
            .format(totalPriceOfProduct) + "."

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
            numberPicker.value = model.quantity.toInt()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

                val newQuantity = numberPicker.value.toLong()
                updateQuantity(newQuantity,model)
                dialog.dismiss()

            }
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {

                db.collection("userShoppingCart").document(model.docId)
                    .delete()
                    .addOnSuccessListener { Log.d("CARTITEMSRECYCLER", "DocumentSnapshot successfully deleted!") }
                    .addOnFailureListener { e -> Log.w("CARTITEMSRECYCLER", "Error deleting document", e) }
                dialog.dismiss()
            }


        }

    }

}