package com.erdees.cakeorderingapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.adapter.MyAccountAdapter
import com.erdees.cakeorderingapp.model.User
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyAccountFragment : Fragment() {


    lateinit var auth : FirebaseAuth
    lateinit var adapter: MyAccountAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.my_acc_fragment, container, false)

        auth = Firebase.auth



        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_account)
         adapter = MyAccountAdapter(auth.currentUser, requireActivity(),parentFragmentManager)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())



        return view
    }

    companion object{
        const val TAG  = "MyAccountFragment"
        fun newInstance(): MyAccountFragment = MyAccountFragment()
    }

}