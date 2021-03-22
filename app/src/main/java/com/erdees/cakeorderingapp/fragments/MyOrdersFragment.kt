package com.erdees.cakeorderingapp.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.MiddleDividerItemDecoration.Companion.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.SharedPreferences
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.adapter.MyOrdersRecyclerAdapter
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.model.Order
import com.erdees.cakeorderingapp.model.Products
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyOrdersFragment : Fragment() {

    private lateinit var activeTextView : TextView
    private lateinit var historyTextView : TextView
    private lateinit var activeTableRow : TableRow
    private lateinit var historyTableRow : TableRow
    private lateinit var standardTextColor : ColorStateList

    private lateinit var recyclerView: RecyclerView
    private lateinit var activeAdapter : MyOrdersRecyclerAdapter
    private lateinit var historyAdapter : MyOrdersRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.my_orders_fragment, container, false)

        /**Binders*/
         activeTextView = view.findViewById(R.id.active_text)
         historyTextView = view.findViewById(R.id.history_text)
         activeTableRow = view.findViewById(R.id.active_table_row)
         historyTableRow = view.findViewById(R.id.history_table_row)
         standardTextColor = activeTextView.textColors // saving default text color
         recyclerView = view.findViewById(R.id.my_orders_recycler_view)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val itemDecoration = DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL)
        //recyclerView.addItemDecoration(itemDecoration)
        /**Firebase*/
        val auth = Firebase.auth
        val db = Firebase.firestore
        /**Shared Preferences*/
        val sharedPreferences = SharedPreferences(requireContext())



        //THIS USER ACTIVE PLACED ORDERS
        val activeQuery = db.collection("placedOrders")
            .whereEqualTo("userId",auth.uid)
            .whereEqualTo("orderStatus","active")
            .orderBy("timestamp")
            .limit(10)
        //THIS USER HISTORY ORDERS
        val historyQuery = db.collection("placedOrders")
            .whereEqualTo("userId",auth.uid)
            .whereEqualTo("orderStatus","completed")
            .orderBy("timestamp")
            .limit(10)
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(5)
            .setPageSize(8)
            .build()
        val activeOptions = FirestorePagingOptions.Builder<Order>()
            .setLifecycleOwner(this)
            .setQuery(activeQuery,config, Order::class.java)
            .build()
        val historyOptions = FirestorePagingOptions.Builder<Order>()
            .setLifecycleOwner(this)
            .setQuery(historyQuery,config,Order::class.java)
            .build()

        /**Buttons*/

        activeTextView.setOnClickListener { activeChosen() }
        historyTextView.setOnClickListener { historyChosen() }
        activeAdapter = MyOrdersRecyclerAdapter(activeOptions,requireActivity(),parentFragmentManager,sharedPreferences)
        historyAdapter = MyOrdersRecyclerAdapter(historyOptions,requireActivity(),parentFragmentManager,sharedPreferences)



        activeChosen() // INITIALLY UI STARTS WITH ACTIVE ORDERS
        return view
    }

    /**Functions*/
    fun activeChosen() {
        activeTextView.setTextColor(resources.getColor(R.color.pink_500))
        activeTableRow.isVisible = true
        historyTextView.setTextColor(standardTextColor)
        historyTableRow.isVisible = false
        recyclerView.adapter = activeAdapter
        //TODO CHANGE ADAPTER
    }

    fun historyChosen() {
        activeTextView.setTextColor(standardTextColor)
        activeTableRow.isVisible = false
        historyTextView.setTextColor(resources.getColor(R.color.pink_500))
        historyTableRow.isVisible = true
        recyclerView.adapter = historyAdapter
        //TODO CHANGE ADAPTER
    }

    companion object {
        const val TAG = "MyOrdersFragment"
        fun newInstance(): MyOrdersFragment = MyOrdersFragment()
    }

}