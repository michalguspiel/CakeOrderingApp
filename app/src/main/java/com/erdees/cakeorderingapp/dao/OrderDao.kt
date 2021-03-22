package androidx.recyclerview.widget.com.erdees.cakeorderingapp.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.erdees.cakeorderingapp.model.Order
import com.erdees.cakeorderingapp.model.Products

class OrderDao {

    private var order: Order? = null
    private val orderLive  = MutableLiveData<Order>()


    init {
        orderLive.value = order
    }
    fun setOrder(orderToPresent: Order){
        order = orderToPresent
        orderLive.value = order
    }

    fun getOrder() = orderLive as LiveData<Order>

}