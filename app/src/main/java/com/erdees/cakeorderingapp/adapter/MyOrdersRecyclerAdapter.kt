package androidx.recyclerview.widget.com.erdees.cakeorderingapp.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
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
    val supportFragmentManager: FragmentManager
): FirestorePagingAdapter<Order,MyOrdersRecyclerAdapter.OrderItemViewHolder>(options) {

    class OrderItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
       val view  = LayoutInflater.from(activity).inflate(R.layout.my_orders_recycler_item,parent,false)
        return OrderItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int, model: Order) {
        val formatTotalPrice = NumberFormat.getCurrencyInstance(Locale.FRANCE).format(model.userShoppingCart.map { it.productPrice * it.quantity }.sum()) // TODO ADD DELIVERY PRICE

        val timeStamptToDate = model.timestamp.toDate().time // TODO FORMAT TIMESTAMP FOR BETTER READABILITY

        val timeStamp = holder.itemView.findViewById<TextView>(R.id.order_item_timestamp)
        val status = holder.itemView.findViewById<TextView>(R.id.order_item_status)
        val price = holder.itemView.findViewById<TextView>(R.id.order_item_price)
        val deliveryMethod = holder.itemView.findViewById<TextView>(R.id.order_item_delivery_method)
        val moreButton = holder.itemView.findViewById<Button>(R.id.order_item_more_button) // TODO OPEN FRAGMENT WITH THIS PARTICULAR ORDER :)


        timeStamp.text = model.timestamp.toDate().toString()
        status.text = model.orderStatus
        price.text = formatTotalPrice
        deliveryMethod.text =  model.deliveryMethod


    }
}