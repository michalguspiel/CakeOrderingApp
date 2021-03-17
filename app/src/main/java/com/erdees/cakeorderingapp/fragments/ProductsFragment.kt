package com.erdees.cakeorderingapp.fragments

import com.erdees.cakeorderingapp.AutoFitGridLayoutManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.MiddleDividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.Utility
import com.erdees.cakeorderingapp.adapter.ProductsAdapter
import com.erdees.cakeorderingapp.model.Products
import com.erdees.cakeorderingapp.viewmodel.ProductsAdapterViewModel
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProductsFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.products_fragment,container,false )

        val db = Firebase.firestore
        val searchTags: Array<String> = arrayOf(String())
        val viewModel: ProductsAdapterViewModel =
            ViewModelProvider(this).get(ProductsAdapterViewModel::class.java)

        val query = db.collection("products")
            .orderBy("productName",Query.Direction.ASCENDING)
            .limit(10)
            //.whereArrayContains("productTags",searchTags)
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(5)
            .setPageSize(8)
            .build()
        val options = FirestorePagingOptions.Builder<Products>()
            .setLifecycleOwner(this)
            .setQuery(query,config,Products::class.java)
            .build()


        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_products)

        val adapter = ProductsAdapter(options,requireActivity(),parentFragmentManager,viewModel)
        val numberOfColumns = Utility().calculateNoOfColumns(requireContext(),170)



        recyclerView.adapter = adapter

        //
         recyclerView.layoutManager = AutoFitGridLayoutManager(requireContext(),500)
        //recyclerView.layoutManager = GridLayoutManager(requireContext(),numberOfColumns)
        val divider = MiddleDividerItemDecoration(requireContext(),MiddleDividerItemDecoration.ALL)
            divider.setDividerColor(R.color.pink_700)
        recyclerView.addItemDecoration(divider)


        return view
    }

    companion object {
        const val TAG = "ProductsFragment"
        fun newInstance(): ProductsFragment = ProductsFragment()
    }
}