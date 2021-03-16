package com.erdees.cakeorderingapp.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.erdees.cakeorderingapp.Constants
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.model.Order
import com.erdees.cakeorderingapp.viewmodel.DeliveryMethodFragmentViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.*

class DeliveryMethodFragment : Fragment() {

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.delivery_method_fragment, container, false)
        val viewModel = ViewModelProvider(this).get(DeliveryMethodFragmentViewModel::class.java)

        val constants = Constants() // constant class so in case of changes ill change only in there


        /**Firebase access*/
        val auth = Firebase.auth
        val db = Firebase.firestore
        val user = auth.currentUser

        /**Binders*/
        val costParanthesis = view.findViewById<TextView>(R.id.delivery_method_cost_plus_delivery)
        val totalCost = view.findViewById<TextView>(R.id.delivery_method_total_cost)
        val radioGroup = view.findViewById<RadioGroup>(R.id.delivery_method_group)
        val radioButtonPickup = view.findViewById<RadioButton>(R.id.delivery_pickup)
        val radioButtonNotPaid = view.findViewById<RadioButton>(R.id.delivery_elves_notpaid)
        val radioButtonPaid = view.findViewById<RadioButton>(R.id.delivery_elves_upfront)
        val confirmPurchaseButton = view.findViewById<Button>(R.id.delivery_method_confirm_button)

        fun format(number: Double): String{
            return NumberFormat.getCurrencyInstance(Locale.FRANCE).format(number)
        }

        val cartCost  = viewModel.getPriceList.value!!.sum()


        val radioListener = RadioGroup.OnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.delivery_pickup -> {
                   totalCost.text = format(cartCost)
                Toast.makeText(requireContext(),"pickup",Toast.LENGTH_SHORT).show()
                }
                R.id.delivery_elves_notpaid -> {
                    totalCost.text = format(cartCost + constants.deliveryUnpaidCost)
                    costParanthesis.text = "(${format(cartCost)} + ${format(constants.deliveryUnpaidCost)})"
                    Toast.makeText(requireContext(),"notpaid",Toast.LENGTH_SHORT).show()
                }
                R.id.delivery_elves_upfront -> {
                    totalCost.text = format(cartCost + constants.deliveryPaidCost)
                    costParanthesis.text = "(${format(cartCost)} + ${format(constants.deliveryPaidCost)})"

                }
                else -> {  Toast.makeText(requireContext(),"ERRR",Toast.LENGTH_SHORT).show()
                }
            }
        }
        radioGroup.setOnCheckedChangeListener(radioListener)
        radioGroup.check(R.id.delivery_pickup) // to start with first button checked
        val mapToPopulate = mutableMapOf<String,Long>()
        val shoppingCartRef = db.collection("userShoppingCart").document(user.uid)
        shoppingCartRef.addSnapshotListener { snapShot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (e != null && snapShot!!.exists()) {
                Log.d("TAG", "Current data: ${snapShot.data}")

            } else {
                Log.d("TAG", "Current data: null")
                shoppingCartRef.get().addOnSuccessListener {documentSnapshot ->
                    documentSnapshot.data?.forEach { data ->
                        val pair = data.key to data.value.toString().toLong()
                        mapToPopulate += pair
                    }
                }
                }
            }

        val orderToPlace = Order(
            mapToPopulate,
            user.uid,
            Timestamp.now(),
            "active"
        )

        confirmPurchaseButton.setOnClickListener {
            when {
                radioButtonPickup.isChecked -> {
                    Toast.makeText(requireContext(),"YOU ORDERED WITH PICKUP",Toast.LENGTH_SHORT).show()

                    db.collection("placedOrders").document().set(orderToPlace)
                    db.collection("userShoppingCart").document(user.uid).delete()
                    parentFragmentManager.popBackStackImmediate()
                    AlertDialog.Builder(requireContext())
                        .setMessage("Your order has been placed successfully.")
                        .setNegativeButton("Back",null)
                        .show()
                }
                radioButtonNotPaid.isChecked -> {
                    Toast.makeText(requireContext(),"ORDERED WITH DELIVERY PAY TO ELVES",Toast.LENGTH_SHORT).show()
                }
                radioButtonPaid.isChecked -> {
                    Toast.makeText(requireContext(),"ORDERED WITH PAID DELIVERY",Toast.LENGTH_SHORT).show()
                }
            }



        }



        return view
    }

    companion object {
        const val TAG = "DeliveryMethodFragment"
        fun newInstance(): DeliveryMethodFragment = DeliveryMethodFragment()
    }
}