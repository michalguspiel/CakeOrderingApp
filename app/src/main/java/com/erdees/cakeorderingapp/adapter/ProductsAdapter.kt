package com.erdees.cakeorderingapp.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.fragments.EachProductFragment
import com.erdees.cakeorderingapp.model.PresentedItem
import com.erdees.cakeorderingapp.model.Products
import com.erdees.cakeorderingapp.openFragment
import com.erdees.cakeorderingapp.viewmodel.ViewModel
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions

class ProductsAdapter(options: FirestorePagingOptions<Products>,val activity: Activity,val supportFragmentManager: FragmentManager,val viewModel: ViewModel):
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
        name.text = model.productName

        Glide.with(activity)
            .load(model.productPictureUrl)
            .override(400, 400)
            .centerCrop()
            .into(picture)

        val layout = holder.itemView.findViewById<RelativeLayout>(R.id.products_recycler_window)

        layout.setOnClickListener{
            viewModel.setProduct(model)
            openFragment(eachProductFragment,EachProductFragment.TAG,supportFragmentManager)


        }

    }


}