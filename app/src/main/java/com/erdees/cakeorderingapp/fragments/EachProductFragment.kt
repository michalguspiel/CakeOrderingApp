package com.erdees.cakeorderingapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.adapter.EachProductAdapter
import com.erdees.cakeorderingapp.model.Products
import com.erdees.cakeorderingapp.randomizeTag
import com.erdees.cakeorderingapp.viewmodel.EachProductAdapterViewModel
import com.erdees.cakeorderingapp.viewmodel.EachProductFragmentViewModel
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.*


class EachProductFragment : Fragment() {
    private lateinit var model: Products
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /**Initialize ui with Viewmodel*/
        val view = inflater.inflate(R.layout.each_product_fragment, container, false)
        val viewModel = ViewModelProvider(this).get(EachProductFragmentViewModel::class.java)




        /**Initialize firestore and get few products *
         * FOR NOW RANDOMIZE TAG AND GETTING PRODUCTS WITH IT
         * * to display at the end*/
        val db = Firebase.firestore
        val query = db.collection("products")
            .whereArrayContains("productTags", "bread") // TODO FOR TESTING THIS IS SET ALWAYS TO BREAD
            .limit(7)

        Log.i(TAG, randomizeTag())

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(10)
            .setPageSize(9)
            .build()
        val options = FirestorePagingOptions.Builder<Products>()
            .setLifecycleOwner(this)
            .setQuery(query, config, Products::class.java)
            .build()


        /**Binders*/
        val image = view.findViewById<ImageView>(R.id.each_product_picture)
        val name = view.findViewById<TextView>(R.id.each_product_name1)
        val desc = view.findViewById<TextView>(R.id.each_product_desc)
        val waitingTime = view.findViewById<TextView>(R.id.each_product_waiting_time)
        val price = view.findViewById<TextView>(R.id.each_product_price)
        val recycler = view.findViewById<RecyclerView>(R.id.each_products_additional_recycler)
        val scrollView = view.findViewById<ScrollView>(R.id.each_product_scroll_view)

        /**SETUP recycler*/
        val adapter = EachProductAdapter(options, requireActivity(), parentFragmentManager,ViewModelProvider(this).get(EachProductAdapterViewModel::class.java))
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        /**Then access the model passed from viewmodel*/
        viewModel.getProduct.observe(viewLifecycleOwner, {
           // scrollView.scrollTo(0,0)
            scrollView.fullScroll(ScrollView.FOCUS_UP)
            model = it
            Glide.with(requireContext())
                .load(model.productPictureUrl)
                .centerCrop()
                .into(image)

            name.text = model.productName
            desc.text = model.productDesc
            price.text = NumberFormat.getCurrencyInstance(Locale.FRANCE).format(model.productPrice)

            waitingTime.text = when (model.productWaitTime) {
                0L -> "Available same day in shop or to be delivered next day."
                else -> "Available within ${model.productWaitTime} days."
            }

        })







        return view
    }

    companion object {
        const val TAG = "EachProductFragment"
        fun newInstance(): EachProductFragment = EachProductFragment()
    }
}