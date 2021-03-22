package androidx.recyclerview.widget.com.erdees.cakeorderingapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.viewmodel.EachOrderFragmentViewModel
import com.erdees.cakeorderingapp.Constants
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.formatNumber

class EachOrderFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.each_order_fragment, container, false)

        /**Access to viewmodel and getting data about what Order to present*/
        val viewModel = ViewModelProvider(this).get(EachOrderFragmentViewModel::class.java)

        /**Binders*/
        val timeStamp = view.findViewById<TextView>(R.id.each_order_time)
        val deliveryMethod = view.findViewById<TextView>(R.id.each_order_delivery_method)
        val deliveryTime = view.findViewById<TextView>(R.id.each_order_delivery_time)
        val deliveryCost = view.findViewById<TextView>(R.id.each_order_delivery_cost)
        val deliveryAddress = view.findViewById<TextView>(R.id.each_order_delivery_address)
        val basketCost = view.findViewById<TextView>(R.id.each_order_basket_cost)
        val totalCost = view.findViewById<TextView>(R.id.each_order_total_cost)
        val recyclerView = view.findViewById<RecyclerView>(R.id.each_order_recycler_view)
        viewModel.getOrder.observe(viewLifecycleOwner, Observer { orderToPresent ->
            /**Set data according to model*/
            timeStamp.text = Constants.timeStampFormat.format(orderToPresent.timestamp)
            deliveryMethod.text = orderToPresent.deliveryMethod
            deliveryTime.text = "It will be delivered when it will be delivered" // TODO Implement actuall functionallity
            deliveryCost.text = "Delivery cost:" + formatNumber(orderToPresent.deliveryPrice)
            deliveryAddress.text = "Delivery address: " + orderToPresent.userAddress // TODO implement logic when order with pickup has different result than order with delivery
            basketCost.text = "Basket cost: " + formatNumber(orderToPresent.priceOfShoppingCart)
            totalCost.text = "Total cost: " + formatNumber(orderToPresent.totalPriceOfOrder())

            /**Set adapter with all items in order*/
        })

        return view
    }


    companion object {
        const val TAG = "EachOrderFragment"
        fun newInstance(): EachOrderFragment = EachOrderFragment()
    }
}