package androidx.recyclerview.widget.com.erdees.cakeorderingapp.repository

import androidx.recyclerview.widget.com.erdees.cakeorderingapp.dao.OrderDao
import com.erdees.cakeorderingapp.model.Order

class OrderRepository(val dao: OrderDao) {


    fun getOrder() = dao.getOrder()
    fun setOrder(orderToSet: Order) = dao.setOrder(orderToSet)
}