package androidx.recyclerview.widget.com.erdees.cakeorderingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.dao.OrderDao
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.repository.OrderRepository
import com.erdees.cakeorderingapp.model.Order

class MyOrdersRecyclerAdapterViewModel(application: Application) : AndroidViewModel(application) {

    val getOrder: LiveData<Order>
    val orderRepository: OrderRepository

    init {
        val orderDao = OrderDao()
        orderRepository = OrderRepository(orderDao)
        getOrder = orderRepository.getOrder()
    }

    fun setOrder(orderToSet : Order) = orderRepository.setOrder(orderToSet)

}