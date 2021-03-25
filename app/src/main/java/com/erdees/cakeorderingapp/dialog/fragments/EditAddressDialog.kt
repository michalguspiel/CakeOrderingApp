package com.erdees.cakeorderingapp.dialog.fragments

import android.graphics.Color
import android.graphics.Color.TRANSPARENT
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.viewmodel.EditAddressDialogViewModel
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.adapter.MyAccountAdapter
import com.erdees.cakeorderingapp.fragments.MyAccountFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.SnackbarContentLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.zip.Inflater

class EditAddressDialog : DialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.edit_address_dialog, container, false)

        val auth = Firebase.auth
        val user = auth.currentUser
        val db = Firebase.firestore

        /**Binders*/
        val addressTextField = view.findViewById<EditText>(R.id.edit_address_field)
        val addressTextField2 = view.findViewById<EditText>(R.id.edit_address_field_2)
        val postCodeTextField = view.findViewById<EditText>(R.id.edit_address_field_post_code)
        val cityTextField = view.findViewById<EditText>(R.id.edit_address_field_city)
        val cancelButton = view.findViewById<Button>(R.id.edit_address_button_cancel)
        val submitButton = view.findViewById<Button>(R.id.edit_address_button_submit)

        if(this.tag == "DeliveryMethodFragment"){

            val viewModel = ViewModelProvider(this).get(EditAddressDialogViewModel::class.java)
            submitButton.setOnClickListener {
              val address: String =  addressTextField.text.toString() +
                             " " + addressTextField2.text.toString() +
                            " " + postCodeTextField.text.toString() +
                                " " + cityTextField.text.toString()
                viewModel.setAddress(address)
                this.dismiss()
            }


        }



        else {
            val docRef = db.collection("users").document(user.uid)
            docRef.get().addOnSuccessListener {
                addressTextField.setText(it["address"].toString())
                addressTextField2.setText(it["address2"].toString())
                postCodeTextField.setText(it["postCode"].toString())
                cityTextField.setText(it["city"].toString())
            }

            submitButton.setOnClickListener {
                docRef
                    .update(
                        "address", addressTextField.text.toString(),
                        "address2", addressTextField2.text.toString(),
                        "postCode", postCodeTextField.text.toString(),
                        "city", cityTextField.text.toString()
                    )
                Toast.makeText(requireContext(), "Updated address", Toast.LENGTH_SHORT).show()
                this.dismiss()
            }
        }
        cancelButton.setOnClickListener { this.dismiss() }

        dialog?.window?.setBackgroundDrawable(ColorDrawable(TRANSPARENT))
        return view
    }

    companion object {
        const val TAG = "EditAddressDialog"
        fun newInstance(): EditAddressDialog = EditAddressDialog()
    }
}