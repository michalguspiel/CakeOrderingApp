package com.erdees.cakeorderingapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.adapter.CartItemsRecyclerAdapter
import com.erdees.cakeorderingapp.model.UserShoppingCart
import com.erdees.cakeorderingapp.openFragment
import com.erdees.cakeorderingapp.viewmodel.MyCartFragmentViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.*

class MyCartFragment : Fragment() {


    val deliveryMethodFragment = DeliveryMethodFragment()
    lateinit var db: FirebaseFirestore
    lateinit var snapshotListener: ListenerRegistration
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.my_cart_fragment, container, false)

        /**ACCESS TO USER AND FIREBASE*/
        val user = Firebase.auth
        db = Firebase.firestore

        /**INITIALIZE VIEWMODEL*/
        val viewModel = ViewModelProvider(this).get(MyCartFragmentViewModel::class.java)


        /**Binders*/
        val recyclerview = view.findViewById<RecyclerView>(R.id.cart_items_recycler)
        val proceedButton = view.findViewById<Button>(R.id.cart_proceed_to_pay)
        val headerText = view.findViewById<TextView>(R.id.my_cart_header_text)
        val cartTotalPrice = view.findViewById<TextView>(R.id.my_cart_total_price)

        var howManyItems = 0
        val priceList = mutableListOf<Double>()


        fun setHeader(itemsAmount: Int): String {
            return if (itemsAmount == 1) "Your shopping cart with $itemsAmount product. \nEnjoy!"
            else if(itemsAmount > 1) "Your shopping cart with $itemsAmount products. \nEnjoy!"
            else "Your basket is empty."
        }

        /**Configurate options in order to populate cart*/
        val query = db.collection("userShoppingCart")
            .whereEqualTo("userId",user.uid)
        val options = FirestoreRecyclerOptions.Builder<UserShoppingCart>()
            .setQuery(query,UserShoppingCart::class.java)
            .setLifecycleOwner(this)
            .build()
        val adapter = CartItemsRecyclerAdapter(
            options,
            this.requireActivity()
        )
        recyclerview.adapter = adapter
        recyclerview.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


       snapshotListener = query.addSnapshotListener{snapshot, error ->
            if(error != null) {
                return@addSnapshotListener
            }
            else{
                howManyItems = 0
                priceList.clear()
                snapshot?.forEach{
                    val eachProductPrice = it["productPrice"].toString().toDouble()
                    val eachProductQuantity = it["quantity"].toString().toInt()
                    val totalPrice = eachProductPrice * eachProductQuantity
                    howManyItems += eachProductQuantity
                    priceList += totalPrice

                }
                headerText.text = setHeader(howManyItems)
                val totalPrice = priceList.sum()
                val totalPriceFormatted = NumberFormat.getCurrencyInstance(Locale.FRANCE).format(totalPrice)
                cartTotalPrice.text = "Price of your shopping cart " + totalPriceFormatted + "."
                viewModel.setPrice(totalPrice)
            }

       }



        proceedButton.setOnClickListener {
            openFragment(deliveryMethodFragment, DeliveryMethodFragment.TAG, parentFragmentManager)
        }

        return view
    }


    override fun onStop() {
        Log.i(EachProductFragment.TAG,"onStop")
        snapshotListener.remove()
        super.onStop()

    }

    companion object {
        const val TAG = "MyCartFragment"
        fun newInstance(): MyCartFragment = MyCartFragment()
    }
}