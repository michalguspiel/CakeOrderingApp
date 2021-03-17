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
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.adapter.CartItemsRecyclerAdapter
import com.erdees.cakeorderingapp.openFragment
import com.erdees.cakeorderingapp.viewmodel.CartItemsRecyclerAdapterViewModel
import com.erdees.cakeorderingapp.viewmodel.MyCartFragmentViewModel
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

        val mapToPopulate = mutableMapOf<String, Long>()
        var howManyItems = 0

        fun setHeader(itemsAmount: Int): String {
            return if (itemsAmount > 0) "Your shopping cart with $itemsAmount products. \nEnjoy!"
            else "Your basket is empty."
        }

        /**Access to user shopping cart
         * take items and amounts
         *
         * Making recycler adapter inside snapshotlistener because data might change when
         * user changes quantity of products or delete products*/
        val docRef = db.collection("userShoppingCart").document(user.uid!!)
        snapshotListener = docRef.addSnapshotListener { snap, error ->
            if (error != null) {
                return@addSnapshotListener
            }
           else {
                howManyItems = 0                                           // to clear item count before counting it again
                mapToPopulate.clear()                                        // clear map before counting
                viewModel.clearPrice()                                       // to clear price before i populate it again
                snap?.data?.forEach { it ->                                 // Iterating over document fields
                        val pair = it.key to it.value.toString().toLong() // Creating a pair in order to add it to mapToPopulate
                        howManyItems += it.value.toString().toInt()         // Counting items in basket
                        mapToPopulate += (pair)                             // adding each pair to map which populates recyclerView
                    }
                    val adapter = CartItemsRecyclerAdapter(
                        mapToPopulate,
                        this.requireActivity(),
                        ViewModelProvider(this).get(CartItemsRecyclerAdapterViewModel::class.java),
                        viewLifecycleOwner
                    )
                    recyclerview.adapter = adapter
                    recyclerview.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    headerText.text = setHeader(howManyItems)
                }
            }
        viewModel.getPriceList.observe(viewLifecycleOwner, {
            val summed = it.sum()
            val totalPriceFormatted = NumberFormat.getCurrencyInstance(Locale.FRANCE).format(summed)
            cartTotalPrice.text = "Price of your shopping cart " + totalPriceFormatted + "."
        })


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