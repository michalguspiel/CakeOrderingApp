package com.erdees.cakeorderingapp.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.model.Products
import com.erdees.cakeorderingapp.openFragment
import com.erdees.cakeorderingapp.viewmodel.EachProductAdapterViewModel
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import java.text.NumberFormat
import java.util.*

/**This is recycler view which is presenting each product
 * starts from header shows name, price, taste variants, ingredients etc.
 * */

class EachProductAdapter( options: FirestorePagingOptions<Products>, val activity: Activity, private val supportFragmentManager: FragmentManager,val viewModel: EachProductAdapterViewModel) :
    FirestorePagingAdapter<Products,EachProductAdapter.ItemViewHolder>(options) {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
                val view = LayoutInflater.from(activity)
                    .inflate(R.layout.each_product_additional_recycler_item, parent, false)
                return ItemViewHolder(view)

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int,model:Products) {
        val picture =
            holder.itemView.findViewById<ImageView>(R.id.each_product_picture_additional_recycler)
        val name =
            holder.itemView.findViewById<TextView>(R.id.each_product_name_additional_recycler)
        val cardView = holder.itemView.findViewById<CardView>(R.id.each_product_additional_recycler_card)
        Glide.with(activity)
            .load(model.productPictureUrl)
            .centerCrop()
            .into(picture)
        name.text = model.productName
        cardView.setOnClickListener {
            Log.i("eachProductAdapter",viewModel.getProduct.value.toString())
            viewModel.addProductToList(viewModel.getProduct.value!!) // to add this viewmodel to list as history // sure this is not null because to get here there has to be something
            viewModel.setProduct(model) // setting a new model for viewmodel
            notifyDataSetChanged()
        }
    }


}
