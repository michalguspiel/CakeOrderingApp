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
import com.erdees.cakeorderingapp.model.User
import com.erdees.cakeorderingapp.model.UserShoppingCart
import com.erdees.cakeorderingapp.viewmodel.DeliveryMethodFragmentViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
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

        /**Firebase access*/

        val auth = Firebase.auth
        val user = auth.currentUser
        val db = Firebase.firestore

        var userAddress = ""
        /**ACCESS TO USER ADDRESS*/
        db.collection("users").document(user.uid).get().addOnSuccessListener { snapshot ->
            val address = snapshot.get("address").toString()
            val address2 = snapshot.get("address2").toString()
            val postCode = snapshot.get("postCode").toString()
            val city = snapshot.get("city").toString()

            val list = listOf<String>(address, address2, postCode, city)
            userAddress = list.joinToString(" ") { it }
        }


        /**Download correct delivery pricing from server*/
        var prePaidDeliveryCost = 0.0
        var paidAtDeliveryCost = 0.0
        val pricesDocRef = db.collection("constants").document("prices")
        pricesDocRef.get().addOnSuccessListener {
            prePaidDeliveryCost = it[Constants.prePaidCost].toString().toDouble()
            paidAtDeliveryCost = it[Constants.paidAtDeliveryCost].toString().toDouble()
        }


        /**Binders*/
        val costParenthesis = view.findViewById<TextView>(R.id.delivery_method_cost_plus_delivery)
        val totalCost = view.findViewById<TextView>(R.id.delivery_method_total_cost)
        val radioGroup = view.findViewById<RadioGroup>(R.id.delivery_method_group)
        val radioButtonPickup = view.findViewById<RadioButton>(R.id.delivery_pickup)
        val radioButtonNotPaid = view.findViewById<RadioButton>(R.id.delivery_elves_notpaid)
        val radioButtonPaid = view.findViewById<RadioButton>(R.id.delivery_elves_upfront)
        val confirmPurchaseButton = view.findViewById<Button>(R.id.delivery_method_confirm_button)

        fun format(number: Double): String {
            return NumberFormat.getCurrencyInstance(Locale.FRANCE).format(number)
        }

        val cartCost = viewModel.getPrice.value!! // TO GET CART COST FROM VIEWMODEL & CART FRAGMENT

        /**Creating list of items
         * then getting query of all of this user "userShoppingCarts"
         * and saving it in value userShoppingItems
         * in order to use it when placing order*/
        val userShoppingItems: MutableList<UserShoppingCart> = mutableListOf()
        val userCart = db.collection("userShoppingCart").whereEqualTo("userId", user.uid)
        userCart.get().addOnSuccessListener { snapshot ->
            snapshot.forEach { userShoppingItems += it.toObject<UserShoppingCart>() }
        }
        userShoppingItems.forEach { Log.i(TAG, it.productName + it.quantity) }


        val radioListener = RadioGroup.OnCheckedChangeListener { group, checkedId ->

            when (checkedId) {
                R.id.delivery_pickup -> {
                    totalCost.text = format(cartCost)
                    costParenthesis.text = ""
                    Toast.makeText(requireContext(), "pickup", Toast.LENGTH_SHORT).show()
                }
                R.id.delivery_elves_notpaid -> {
                    userShoppingItems.forEach { Log.i(TAG, it.productName + it.quantity) }

                    totalCost.text = format(cartCost + paidAtDeliveryCost)
                    costParenthesis.text = "(${format(cartCost)} + ${format(paidAtDeliveryCost)})"
                    Toast.makeText(requireContext(), "notpaid", Toast.LENGTH_SHORT).show()
                }
                R.id.delivery_elves_upfront -> {
                    totalCost.text = format(cartCost + prePaidDeliveryCost)
                    costParenthesis.text = "(${format(cartCost)} + ${format(prePaidDeliveryCost)})"

                }
                else -> {
                    Toast.makeText(requireContext(), "ERRR", Toast.LENGTH_SHORT).show()
                }
            }
        }
        radioGroup.setOnCheckedChangeListener(radioListener)
        radioGroup.check(R.id.delivery_pickup) // to start with first button checked
        val mapToPopulate = mutableMapOf<String, Long>()
        val shoppingCartRef = db.collection("userShoppingCart").document(user.uid)
        shoppingCartRef.addSnapshotListener { snapShot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (e != null && snapShot!!.exists()) {
                Log.d("TAG", "Current data: ${snapShot.data}")

            } else {
                Log.d("TAG", "Current data: null")
                shoppingCartRef.get().addOnSuccessListener { documentSnapshot ->
                    documentSnapshot.data?.forEach { data ->
                        val pair = data.key to data.value.toString().toLong()
                        mapToPopulate += pair
                    }
                }
            }
        }


        /**FUNCTIONS PLACE ORDER AND DELETE EVERY ITEM FROM USERSHOPPINGCART*/

        fun placeOrder(order: Order) {

            db.collection("placedOrders").document().set(order)
        }

        fun cleanShoppingCart() {
            userCart.get().addOnSuccessListener { snapShot ->
                snapShot.forEach {
                    db.collection("userShoppingCart").document(it.id).delete()
                }
            }
        }

        val notificationDialog =
            AlertDialog.Builder(requireContext())
            .setMessage("Your order has been placed successfully.")
            .setOnDismissListener {
                parentFragmentManager.popBackStackImmediate()
            }
            .setNegativeButton("Back", null)

        confirmPurchaseButton.setOnClickListener {

            val orderToPlace: Order = Order(
                userShoppingItems,
                user.uid,
                "pickup",
                false,
                Timestamp.now(),
                Timestamp.now(), // TODO in future picking up date
                userAddress,
                "active",
                0.0
            )

            when {
                radioButtonPickup.isChecked -> {
                    placeOrder(orderToPlace)
                    cleanShoppingCart()
                    notificationDialog.show()
                }
                radioButtonNotPaid.isChecked -> {
                    if(userAddress.isNullOrBlank()) {
                        Toast.makeText(
                            requireContext(),
                            "Your account doesn't have any address where we can deliver.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
                    orderToPlace.deliveryMethod = "Delivery unpaid"
                    placeOrder(orderToPlace)
                    cleanShoppingCart()
                    notificationDialog.show()
                }
                radioButtonPaid.isChecked -> {
                    Toast.makeText(
                        requireContext(),
                        "ORDERED WITH PAID DELIVERY",
                        Toast.LENGTH_SHORT
                    ).show()
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