package com.erdees.cakeorderingapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.adapter.CartItemsRecyclerAdapter
import com.erdees.cakeorderingapp.viewmodel.CartItemsRecyclerAdapterViewModel
import com.erdees.cakeorderingapp.viewmodel.MyCartFragmentViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.*

class MyCartFragment: Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.my_cart_fragment,container,false)

        /**ACCESS TO USER AND FIREBASE*/
        val user = Firebase.auth
        val db = Firebase.firestore

        /**INITIALIZE VIEWMODEL*/
        val viewModel = ViewModelProvider(this).get(MyCartFragmentViewModel::class.java)


        /**Binders*/
        val recyclerview = view.findViewById<RecyclerView>(R.id.cart_items_recycler)
        val proceedButton = view.findViewById<Button>(R.id.cart_proceed_to_pay)
        val headerText = view.findViewById<TextView>(R.id.my_cart_header_text)
        val cartTotalPrice = view.findViewById<TextView>(R.id.my_cart_total_price)

        val mapToPopulate = mutableMapOf<String,Long>()
        var howManyItems = 0

        fun setHeader(itemsAmount: Int):String{
            return if(itemsAmount>0) "Your shopping cart with $itemsAmount products. \nEnjoy!"
            else "Your basket is empty."
        }

        /**Access to user shopping cart
         * take items and amounts*/
        db.collection("userShoppingCart").document(user.uid!!).get().addOnSuccessListener { snapshot ->
            snapshot.data!!.forEach {
            val pair = it.key to it.value.toString().toLong()
                howManyItems += it.value.toString().toInt()
                mapToPopulate += (pair)
                Log.i("cart",pair.first + pair.second.toString())
                Log.i("cart2",mapToPopulate.size.toString())
            }
            viewModel.clearPrice() // to clear price before i populate it again
            val adapter =  CartItemsRecyclerAdapter(mapToPopulate,requireActivity(),ViewModelProvider(this).get(CartItemsRecyclerAdapterViewModel::class.java),viewLifecycleOwner)
            recyclerview.adapter = adapter
            recyclerview.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            headerText.text = setHeader(howManyItems)
        }

        viewModel.getPriceList.observe(viewLifecycleOwner, Observer {
            val summed = it.sum()
            val totalPriceFormatted = NumberFormat.getCurrencyInstance(Locale.FRANCE).format(summed)
            cartTotalPrice.text = "Price of your shopping cart " + totalPriceFormatted + "."
        })


        proceedButton.setOnClickListener {


        }

        return view
    }


    companion object{
        const val TAG = "MyCartFragment"
        fun newInstance() : MyCartFragment = MyCartFragment()
    }
}