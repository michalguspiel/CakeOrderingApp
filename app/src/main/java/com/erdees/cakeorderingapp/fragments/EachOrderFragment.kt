package androidx.recyclerview.widget.com.erdees.cakeorderingapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.erdees.cakeorderingapp.R

class EachOrderFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.each_order_fragment,container,false)

        return view
    }


    companion object{
        const val TAG = "EachOrderFragment"
        fun newInstance():  EachOrderFragment = EachOrderFragment()
    }
}