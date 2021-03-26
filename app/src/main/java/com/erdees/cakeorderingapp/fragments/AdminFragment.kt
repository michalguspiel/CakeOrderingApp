package androidx.recyclerview.widget.com.erdees.cakeorderingapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.openFragment

class AdminFragment: Fragment() {

    val adminOrdersFragment = AdminOrdersFragment()
    val adminAddProductsFragment = AdminAddProductsFragment()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.admin_fragment,container,false)

        val checkOrders = view.findViewById<Button>(R.id.admin_check_orders_button)
        val addProdcuts = view.findViewById<Button>(R.id.admin_add_products_button)


        checkOrders.setOnClickListener {
            openFragment(adminOrdersFragment,AdminOrdersFragment.TAG,parentFragmentManager, R.id.admin_container)
        }


        addProdcuts.setOnClickListener{
            openFragment(adminAddProductsFragment,AdminAddProductsFragment.TAG,parentFragmentManager,R.id.admin_container)
        }

        return view
    }
    companion object {
        const val TAG = "AdminFragment"
        fun newInstance(): AdminFragment = AdminFragment()
    }
}