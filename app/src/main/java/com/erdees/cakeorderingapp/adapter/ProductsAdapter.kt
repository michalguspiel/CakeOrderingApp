package com.erdees.cakeorderingapp.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.fragments.EachProductFragment
import com.erdees.cakeorderingapp.model.Products
import com.erdees.cakeorderingapp.openFragment
import com.erdees.cakeorderingapp.viewmodel.ProductsAdapterViewModel
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import java.text.NumberFormat
import java.util.*

class ProductsAdapter(options: FirestorePagingOptions<Products>,
                      val activity: Activity,
                      val supportFragmentManager: FragmentManager,
                      val viewModel: ProductsAdapterViewModel):
    FirestorePagingAdapter<Products, ProductsAdapter.ProductsItemViewHolder>(options) {


    val eachProductFragment = EachProductFragment()

    class ProductsItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsItemViewHolder {
         val view =  LayoutInflater.from(activity).inflate(R.layout.products_recycler_item,parent,false)
         return ProductsItemViewHolder(view)
    }


    override fun onBindViewHolder(holder: ProductsItemViewHolder, position: Int, model: Products) {
        val name =  holder.itemView.findViewById<TextView>(R.id.product_title)
        val picture = holder.itemView.findViewById<ImageView>(R.id.product_picture)
        val price = holder.itemView.findViewById<TextView>(R.id.product_price)
        name.text = model.productName
        price.text = NumberFormat.getCurrencyInstance(Locale.FRANCE).format(model.productPrice)
        Glide.with(activity)
            .load(model.productPictureUrl)
            .override(480, 480)
            .centerCrop()
            .into(picture)

        val layout = holder.itemView.findViewById<RelativeLayout>(R.id.products_recycler_window)

        layout.setOnClickListener{
            viewModel.setProduct(model)
            openFragment(eachProductFragment,EachProductFragment.TAG,supportFragmentManager,R.id.container)


        }

    }


}