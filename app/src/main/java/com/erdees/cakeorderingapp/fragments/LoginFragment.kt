package com.erdees.cakeorderingapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.erdees.cakeorderingapp.R

class LoginFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.activity_main, container, false)






        return view
    }
companion object{
    const val TAG = "LoginFragment"
    fun newInstance():LoginFragment
    = LoginFragment()
}

}