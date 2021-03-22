package androidx.recyclerview.widget.com.erdees.cakeorderingapp.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.fragments.EachOrderFragment
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.viewmodel.MyOrdersRecyclerAdapterViewModel
import com.erdees.cakeorderingapp.Constants
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.model.Order
import com.erdees.cakeorderingapp.openFragment
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import java.text.NumberFormat
import java.util.*
import java.util.zip.Inflater

class MyOrdersRecyclerAdapter(
    options: FirestorePagingOptions<Order>,
    val activity: Activity,
    val supportFragmentManager: FragmentManager,
    val viewModel: MyOrdersRecyclerAdapterViewModel
) : FirestorePagingAdapter<Order, MyOrdersRecyclerAdapter.OrderItemViewHolder>(options) {

    class OrderItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    val eachOrderFragment = EachOrderFragment()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view =
            LayoutInflater.from(activity).inflate(R.layout.my_orders_recycler_item, parent, false)
        return OrderItemViewHolder(view)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int, model: Order) {


        val formatTotalPrice = NumberFormat.getCurrencyInstance(Locale.FRANCE)
            .format(model.totalPriceOfOrder())
        val timeStamp = holder.itemView.findViewById<TextView>(R.id.order_item_timestamp)
        val status = holder.itemView.findViewById<TextView>(R.id.order_item_status)
        val price = holder.itemView.findViewById<TextView>(R.id.order_item_price)
        val deliveryMethod = holder.itemView.findViewById<TextView>(R.id.order_item_delivery_method)
        val moreButton =
            holder.itemView.findViewById<TextView>(R.id.order_item_more_button)


        timeStamp.text = Constants.timeStampFormat.format(model.timestamp.toDate())
        status.text = model.orderStatus
        price.text = formatTotalPrice
        deliveryMethod.text = model.deliveryMethod


        moreButton.setOnClickListener {
            viewModel.setOrder(model) // SEND THIS MODEL TO VIEWMODEL IN ORDER TO PRESENT IT IN [EachOrderFragment]
            openFragment(eachOrderFragment,EachOrderFragment.TAG,supportFragmentManager)
        }

    }
}