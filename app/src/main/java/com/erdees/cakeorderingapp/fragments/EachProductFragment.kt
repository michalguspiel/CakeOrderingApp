package com.erdees.cakeorderingapp.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.net.sip.SipSession
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.adapter.EachProductAdapter
import com.erdees.cakeorderingapp.model.Products
import com.erdees.cakeorderingapp.model.UserShoppingCart
import com.erdees.cakeorderingapp.randomizeTag
import com.erdees.cakeorderingapp.viewmodel.EachProductAdapterViewModel
import com.erdees.cakeorderingapp.viewmodel.EachProductFragmentViewModel
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.*
import kotlin.collections.HashMap


class EachProductFragment : Fragment() {
    private lateinit var model: Products
    private lateinit var snapshotListener : ListenerRegistration

    private lateinit var addToCartButton : Button
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
        val auth = Firebase.auth


        /**NEED TO CHECK IF USER HAS THIS PRODUCT IN HIS CART
         * IF YES THEN HOW MANY
         * SO I CAN SUM IT WITH AMOUNT THAT USER WANTS TO ADD RIGHT NOW
         *
         * LETS SAY WHEN USER HAS 3 CHOCO MUFFINS ALREADY IN HIS CART
         * AND WANT TO ADD 2 MORE
         * THIS COMPUTES INTO 5 AND THEN OVERRIDES THAT 2 IN CART WITH NEW AMOUNT
         *
         * FOR NOW SWITCHING THIS OPTION OFF BUT IN FUTURE I CAN RE IMPLEMENT IT
         * WHY SWITCH IT OFF? BECAUSE WITH CURRENT DATABASE ARCHITECTURE I CAN HAVE MANY SAME PRODUCTS IN BASKET UNDER DIFFRENT ID
         *
        var amountOfThisProductalreadyInCart = 0L
        val docRef = db.collection("userShoppingCart").document(auth.uid?)
           snapshotListener =  docRef.addSnapshotListener{ snapShot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                if (e != null && snapShot!!.exists()) {
                    Log.d("TAG", "Current data: ${snapShot.data}")
                } else {
                    docRef.get().addOnSuccessListener {
                        if (it[model.docId] != null) {
                            amountOfThisProductalreadyInCart = it[model.docId].toString().toLong()
                        }
                    }
                }
            }
*/

        /**Binders*/
        val image = view.findViewById<ImageView>(R.id.each_product_picture)
        val name = view.findViewById<TextView>(R.id.each_product_name1)
        val desc = view.findViewById<TextView>(R.id.each_product_desc)
        val waitingTime = view.findViewById<TextView>(R.id.each_product_waiting_time)
        val price = view.findViewById<TextView>(R.id.each_product_price)
        val ingredientList = view.findViewById<TextView>(R.id.each_product_ingredients)
        val recycler = view.findViewById<RecyclerView>(R.id.each_products_additional_recycler)
        val scrollView = view.findViewById<ScrollView>(R.id.each_product_scroll_view)
        addToCartButton = view.findViewById(R.id.each_product_add_to_cart_button)
        setUI(auth.currentUser != null)


        /**Access the model passed from viewmodel*/
        viewModel.getProduct.observe(viewLifecycleOwner, {
            scrollView.fullScroll(ScrollView.FOCUS_UP)
            model = it
            Glide.with(requireContext())
                .load(model.productPictureUrl)
                .centerCrop()
                .into(image)

            name.text = model.productName
            desc.text = model.productDesc
            price.text = NumberFormat.getCurrencyInstance(Locale.FRANCE).format(model.productPrice)
            ingredientList.text = "Product includes: " + model.productIngredients.joinToString(", ") { it } + "."


            waitingTime.text = when (model.productWaitTime) {
                0L -> "Available same day in shop or to be delivered next day."
                else -> "Available within ${model.productWaitTime} days."
            }


            addToCartButton.setOnClickListener {
                val numberPicker = NumberPicker(requireContext())
                numberPicker.minValue = 1
                numberPicker.maxValue = 999
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("How many")
                .setView(numberPicker)
                .setPositiveButton("Add to cart",null)
                .setNegativeButton("Cancel",null)
                .show()
            dialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener {
                Log.i("test",numberPicker.value.toString())
                val cartMap = HashMap<String, Any>()
                cartMap.put("productId",model.docId)
                cartMap.put("productName",model.productName)
                cartMap.put("userId",auth.currentUser.uid)
                cartMap.put("productPrice",model.productPrice)
                cartMap.put("quantity",numberPicker.value.toLong())
                cartMap.put("productPictureUrl",model.productPictureUrl)
                    db.collection("userShoppingCart")
                        .document().set(cartMap, SetOptions.merge())

                dialog.dismiss()
            }
            }


            /**All of these is inside observe because it needs initialized model to
             * not display in bottom recycler view product that is presented on top */
            val query = db.collection("products")
                .whereArrayContains("productTags", randomizeTag()) // TODO FOR TESTING THIS IS SET ALWAYS TO BREAD
                .limit(7)
                .whereNotEqualTo("productName",model.productName)
            val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(9)
                .build()
            val options = FirestorePagingOptions.Builder<Products>()
                .setLifecycleOwner(this)
                .setQuery(query, config, Products::class.java)
                .build()

            /**Setting up recycler*/
            val adapter = EachProductAdapter(options, requireActivity(), parentFragmentManager,ViewModelProvider(this).get(EachProductAdapterViewModel::class.java))
            recycler.adapter = adapter
            recycler.layoutManager = LinearLayoutManager(
                requireActivity(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        })



        return view
    }

    fun setUI(isUserLogedIn: Boolean){
        addToCartButton.isEnabled = isUserLogedIn
    }


    override fun onStop() {
        Log.i(TAG,"onStop")
       // snapshotListener.remove()
        super.onStop()
    }

    companion object {
        const val TAG = "EachProductFragment"
        fun newInstance(): EachProductFragment = EachProductFragment()
    }
}