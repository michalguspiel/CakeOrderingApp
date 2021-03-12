package com.erdees.cakeorderingapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.model.Products
import com.erdees.cakeorderingapp.viewmodel.ViewModel

class EachProductFragment : Fragment() {
    private lateinit var model: Products
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /**Initialize ui with Viewmodel*/
        val view = inflater.inflate(R.layout.each_product_fragment, container, false)
        val viewModel: ViewModel = ViewModelProvider(this).get(ViewModel::class.java)

        val name = view.findViewById<TextView>(R.id.each_product_name)

        /**Then access the model passed from viewmodel*/
        viewModel.getProduct.observe(viewLifecycleOwner, Observer { model = it
            name.text = model.productName})





        return view
    }

    companion object {
        const val TAG = "EachProductFragment"
        fun newInstance(): EachProductFragment = EachProductFragment()
    }
}