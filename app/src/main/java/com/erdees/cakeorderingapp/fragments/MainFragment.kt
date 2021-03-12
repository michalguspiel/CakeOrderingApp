package com.erdees.cakeorderingapp.fragments

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.adapter.MainActivityRecyclerAdapter
import com.erdees.cakeorderingapp.model.PresentedItem
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.model.DatabaseId
import com.google.firebase.ktx.Firebase

class MainFragment : Fragment() {


    lateinit var auth : FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.main_fragment, container, false)

        auth = Firebase.auth
        val db = Firebase.firestore

        /**Get Screen width*/
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels

        /**Get products to populate main recycler view*/
        val presentationQuery = db.collection("productsForRecycler")
            .orderBy("order", Query.Direction.ASCENDING)
            .limit(10)
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(10)
            .setPageSize(8)
            .build()
        // The options for the adapter combine the paging configuration with query information
        // and application-specific options for lifecycle, etc.
        val options = FirestorePagingOptions.Builder<PresentedItem>()
            .setLifecycleOwner(this)
            .setQuery(presentationQuery, config, PresentedItem::class.java)
            .build()
        /**Setup recycler view*/
        val adapter = MainActivityRecyclerAdapter(requireActivity(), options, screenWidth,parentFragmentManager)
        val recyclerView = view.findViewById<RecyclerView>(R.id.mainRecyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())



        return view
    }

    companion object {
        const val TAG = "MainFragment"
        fun newInstance(): MainFragment =
            MainFragment()
    }
}