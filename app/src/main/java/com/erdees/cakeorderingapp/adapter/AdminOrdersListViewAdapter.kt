package androidx.recyclerview.widget.com.erdees.cakeorderingapp.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.model.User
import com.erdees.cakeorderingapp.model.UserShoppingCart

class AdminOrdersListViewAdapter(val listOfItems: List<UserShoppingCart>,val  context: Activity):
    ArrayAdapter<UserShoppingCart>(context, R.layout.listview_row,listOfItems) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        Log.i("TEST","row created!")

        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.listview_row,
            parent, false)

        val amount = rowView.findViewById<TextView>(R.id.product_amount_dishrow)
        val name = rowView.findViewById<TextView>(R.id.product_name_dishrow)

        name.text = listOfItems[position].productName
        amount.text = listOfItems[position].quantity.toString()



        return rowView

    }

}