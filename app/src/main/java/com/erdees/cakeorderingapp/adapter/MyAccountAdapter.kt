package com.erdees.cakeorderingapp.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.dialog.fragments.EditAddressDialog
import com.erdees.cakeorderingapp.model.User
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyAccountAdapter(val user: FirebaseUser,val activity: Activity, val parentFragmentManager: FragmentManager) :
    RecyclerView.Adapter<MyAccountAdapter.ItemViewHolder>() {
    val db = Firebase.firestore
    val auth = Firebase.auth
    val editAddressDialog = EditAddressDialog.newInstance()
    val docRef = db.collection("users").document(user.uid)
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return when (viewType) {
            0 -> {
                val view = LayoutInflater.from(activity).inflate(R.layout.main_account_first_item,parent,false)
                ItemViewHolder(view)
            }
            1 -> {
                val view = LayoutInflater.from(activity).inflate(R.layout.main_account_address_item,parent,false)
                ItemViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(activity).inflate(R.layout.main_account_loyal_points,parent,false)
                ItemViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        when (getItemViewType(position)) {
            0 -> {
                val textView = holder.itemView.findViewById<TextView>(R.id.account_name_text_view)
                if (user.displayName != null) textView.text = user.displayName
                else textView.text = user.email
            }
            1 -> {
                val addressTextView =
                    holder.itemView.findViewById<TextView>(R.id.account_address_text_view)
                val postcodeTextView =
                    holder.itemView.findViewById<TextView>(R.id.account_postcode_text_view)
                val cityTextView =
                    holder.itemView.findViewById<TextView>(R.id.account_city_text_view)
                val editAddressButton =
                    holder.itemView.findViewById<Button>(R.id.edit_address_button)


                Log.i("Eligible",user.uid)

                docRef.addSnapshotListener { snapShot, e ->
                    if (e != null) {
                        return@addSnapshotListener
                    }
                    if (e != null && snapShot!!.exists()) {
                        Log.d("TAG", "Current data: ${snapShot.data}")

                    } else {
                        Log.d("TAG", "Current data: null")
                        docRef.get().addOnSuccessListener {
                            addressTextView.text = it["address"].toString() +" "+ it["address2"].toString()
                            postcodeTextView.text = it["postCode"].toString()
                            cityTextView.text = it["city"].toString()
                        }
                    }
                }


                editAddressButton.setOnClickListener {
                    editAddressDialog.show(
                        parentFragmentManager.beginTransaction(),
                        EditAddressDialog.TAG
                    )
                }

            }
            else -> {
                val points = holder.itemView.findViewById<TextView>(R.id.account_user_points)

                docRef.addSnapshotListener { snapShot, e ->
                    if (e != null) {
                        return@addSnapshotListener
                    }
                    if (e != null && snapShot!!.exists()) {
                        Log.d("TAG", "Current data: ${snapShot.data}")

                    } else {
                        Log.d("TAG", "Current data: null")
                        docRef.get().addOnSuccessListener {

                            if (it["points"] == null) points.text = 0.toString()
                            else points.text = it["points"].toString()
                        }
                    }
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> 0
            1 -> 1
            else -> 2
        }
    }


}