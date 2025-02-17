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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.adapter.EachOrderCartItemsAdapter
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
        viewModel.getOrder.observe(viewLifecycleOwner,  { orderToPresent ->
            /**Set data according to model*/
            timeStamp.text = Constants.timeStampFormat.format(orderToPresent.timestamp.toDate())
            deliveryMethod.text ="Delivery method: " + orderToPresent.deliveryMethod
            deliveryTime.text = setWhenReady(orderToPresent.deliveryMethod,orderToPresent.orderToBeReadyDate)
            deliveryCost.text = formatNumber(orderToPresent.deliveryPrice)
            deliveryAddress.text = setOrderAddress(orderToPresent.userAddress,orderToPresent.deliveryMethod)
            basketCost.text = formatNumber(orderToPresent.priceOfShoppingCart)
            totalCost.text = formatNumber(orderToPresent.totalPriceOfOrder())

            /**Set adapter with all items in order*/

            val adapter = EachOrderCartItemsAdapter(orderToPresent.userShoppingCart,requireActivity())
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)

        })

        return view
    }

    fun setOrderAddress(address: String,deliveryMethod: String): String{
        return when(deliveryMethod) {
            "Pickup" -> ""
            else ->"Delivery address: " + address
        }

    }

    fun setWhenReady(deliveryMethod: String,readyDate:String):String {
        return when(deliveryMethod) {
            "Pickup" -> "Ready to pickup: $readyDate"
            else -> "Delivery date: $readyDate"
        }
    }


    companion object {
        const val TAG = "EachOrderFragment"
        fun newInstance(): EachOrderFragment = EachOrderFragment()
    }
}