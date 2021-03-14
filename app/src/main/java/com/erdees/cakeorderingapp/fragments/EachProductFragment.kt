package com.erdees.cakeorderingapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.adapter.EachProductAdapter
import com.erdees.cakeorderingapp.model.Products
import com.erdees.cakeorderingapp.viewmodel.EachProductAdapterViewModel

class EachProductFragment : Fragment() {
    private lateinit var model: Products
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /**Initialize ui with Viewmodel*/
        val view = inflater.inflate(R.layout.each_product_fragment, container, false)
        val viewModel = ViewModelProvider(this).get(EachProductAdapterViewModel::class.java)

        /**Then access the model passed from viewmodel*/
        viewModel.getProduct.observe(viewLifecycleOwner, { model = it
            val recyclerView = view.findViewById<RecyclerView>(R.id.each_product_recycler)
            val adapter = EachProductAdapter(model,requireActivity())
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            })







        return view
    }

    companion object {
        const val TAG = "EachProductFragment"
        fun newInstance(): EachProductFragment = EachProductFragment()
    }
}