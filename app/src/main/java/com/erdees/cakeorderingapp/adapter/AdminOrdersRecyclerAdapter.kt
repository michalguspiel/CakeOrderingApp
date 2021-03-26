package androidx.recyclerview.widget.com.erdees.cakeorderingapp.adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.erdees.cakeorderingapp.Constants
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.model.Order
import com.erdees.cakeorderingapp.model.UserShoppingCart
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.*

class AdminOrdersRecyclerAdapter(options: FirestoreRecyclerOptions<Order>,val context: Context, val activity: Activity):
    FirestoreRecyclerAdapter<Order, AdminOrdersRecyclerAdapter.ItemViewHolder>(options) {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    val db = Firebase.firestore
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.admin_orders_item, parent, false)

        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int, model: Order) {

        val refToThisOrder = db.collection("placedOrders").document(model.docId)

        val formatTotalPrice = NumberFormat.getCurrencyInstance(Locale.FRANCE)
            .format(model.totalPriceOfOrder())
        val timeStamp = holder.itemView.findViewById<TextView>(R.id.admin_order_item_timestamp)
        val status = holder.itemView.findViewById<TextView>(R.id.admin_order_item_status)
        val price = holder.itemView.findViewById<TextView>(R.id.admin_order_item_price)
        val deliveryMethod =
            holder.itemView.findViewById<TextView>(R.id.admin_order_item_delivery_method)
        val deliveryAddress = holder.itemView.findViewById<TextView>(R.id.admin_order_item_address)
        val orderedByUser = holder.itemView.findViewById<TextView>(R.id.admin_order_item_user_id)
        val markAsCompleted =
            holder.itemView.findViewById<TextView>(R.id.admin_order_item_mark_as_completed_button)

        val listView = holder.itemView.findViewById<ListView>(R.id.admin_order_item_list_view)
        val wholeLayout = holder.itemView.findViewById<CardView>(R.id.admin_orders_card_view)

        timeStamp.text = Constants.timeStampFormat.format(model.timestamp.toDate())
        status.text = model.orderStatus
        price.text = formatTotalPrice
        deliveryMethod.text = model.deliveryMethod
        deliveryAddress.text = setAddress(model.userAddress, model.deliveryMethod)

        orderedByUser.text = "Ordered by: ${model.userName}."


        markAsCompleted.setOnClickListener {
           val alertDialog =  AlertDialog.Builder(context).setMessage("Marking this order as completed is not reversible.")
                .setPositiveButton("Continue",null)
                .setNegativeButton("Back",null)
                .show()

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                refToThisOrder.update("orderStatus","completed")
                alertDialog.dismiss()
                // update placedorder.

            }
        }


        wholeLayout.setOnClickListener {
            /**If listview doesn't have adapter, make it and attach, if has detach.*/
            Log.i("TEST","clicked")
            if (listView.adapter == null) {
                val listViewAdapter = AdminOrdersListViewAdapter(model.userShoppingCart, activity)
                listView.adapter = listViewAdapter
                Log.i("TEST","adapter made and set!")

                listView.layoutParams =
                    LinearLayout.LayoutParams(listView.layoutParams.width, getListSize(model.userShoppingCart,listView))
            } else {
                listView.adapter = null
                listView.layoutParams =
                    LinearLayout.LayoutParams(listView.layoutParams.width, 0)
            }
    }
    }


    fun setAddress(address: String, deliveryMethod: String): String {
        return when (deliveryMethod) {
            "Pickup" -> "It's a pickup"
            else -> "Delivery address: $address."
        }
    }


    /**Computes height of listView based on each row height, includes dividers.
     * I'm using this approach so listView size is set and doesn't need to be scrollable. */
    fun getListSize(list: List<UserShoppingCart>,listView: ListView): Int {
        var result = 0
        for (eachProduct in list.indices) {
            val listItem = listView.adapter.getView(eachProduct, null, listView)
            listItem.measure(0, View.MeasureSpec.UNSPECIFIED)
            result += listItem.measuredHeight
        }
        return result + (listView.dividerHeight * (listView.adapter.count - 1))
    }


}