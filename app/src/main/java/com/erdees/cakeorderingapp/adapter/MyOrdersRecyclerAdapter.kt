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
import com.erdees.cakeorderingapp.Constants
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.model.Order
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import java.text.NumberFormat
import java.util.*
import java.util.zip.Inflater

class MyOrdersRecyclerAdapter(
    options: FirestorePagingOptions<Order>,
    val activity: Activity,
    val supportFragmentManager: FragmentManager,
    val sharedPreferences: androidx.recyclerview.widget.com.erdees.cakeorderingapp.SharedPreferences
) : FirestorePagingAdapter<Order, MyOrdersRecyclerAdapter.OrderItemViewHolder>(options) {

    class OrderItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    val pickUpDeliveryCost = 0.0
    val prePaidDeliveryCost =
        sharedPreferences.getValueString(Constants.sharedPrefPaidDeliveryCost)?.toDouble()
    val unPaidDeliveryCost =
        sharedPreferences.getValueString(Constants.sharedPrefUnpaidDeliveryCost)?.toDouble()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view =
            LayoutInflater.from(activity).inflate(R.layout.my_orders_recycler_item, parent, false)
        return OrderItemViewHolder(view)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int, model: Order) {


        val formatTotalPrice = NumberFormat.getCurrencyInstance(Locale.FRANCE)
            .format(model.totalPriceOfOrder())
        val timeStampFormat = java.text.SimpleDateFormat("dd-MM-yyyy HH:mm")
        val timeStamp = holder.itemView.findViewById<TextView>(R.id.order_item_timestamp)
        val status = holder.itemView.findViewById<TextView>(R.id.order_item_status)
        val price = holder.itemView.findViewById<TextView>(R.id.order_item_price)
        val deliveryMethod = holder.itemView.findViewById<TextView>(R.id.order_item_delivery_method)
        val moreButton =
            holder.itemView.findViewById<TextView>(R.id.order_item_more_button) // TODO OPEN FRAGMENT WITH THIS PARTICULAR ORDER :)


        timeStamp.text = timeStampFormat.format(model.timestamp.toDate())
        status.text = model.orderStatus
        price.text = formatTotalPrice
        deliveryMethod.text = model.deliveryMethod


    }
}