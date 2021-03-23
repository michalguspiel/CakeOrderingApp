package androidx.recyclerview.widget.com.erdees.cakeorderingapp.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.formatNumber
import com.erdees.cakeorderingapp.model.UserShoppingCart

/**Adapter which populates recycler view inside each order fragment
 * I could try to use same adapter as
 * CartItemsRecyclerAdapter
 * but there's no point of using options, all I need to do is to take
 * shopping cart list from order and pass it here
 * */
class EachOrderCartItemsAdapter(
    val itemList: List<UserShoppingCart>,
    val activity: Activity
) : RecyclerView.Adapter<EachOrderCartItemsAdapter.ItemViewHolder>() {
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.each_order_recycler_item,parent,false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val image = holder.itemView.findViewById<ImageView>(R.id.each_order_recycler_item_image)
        val name = holder.itemView.findViewById<TextView>(R.id.each_order_recycler_item_name)
        val quantity = holder.itemView.findViewById<TextView>(R.id.each_order_recycler_item_quantity)
        val totalPrice = holder.itemView.findViewById<TextView>(R.id.each_order_recycler_item_total_price)

        name.text = itemList[position].productName
        quantity.text ="Quantity: " + itemList[position].quantity.toString()
        val totalPriceOfProduct = itemList[position].productPrice * itemList[position].quantity
        totalPrice.text ="Total price: " +  formatNumber(totalPriceOfProduct)

        Glide.with(activity)
            .load(itemList[position].productPictureUrl)
            .override(100, 100)
            .centerCrop()
            .into(image)
    }

    override fun getItemCount(): Int {
       return  itemList.size
    }

}