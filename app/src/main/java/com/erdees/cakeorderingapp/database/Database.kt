package com.erdees.cakeorderingapp.database

import androidx.recyclerview.widget.com.erdees.cakeorderingapp.dao.AddressDao
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.dao.CalendarDayDao
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.dao.OrderDao
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.dao.QueryDao
import com.erdees.cakeorderingapp.dao.PriceDao
import com.erdees.cakeorderingapp.dao.ProductDao

class Database private constructor(){

    var productDao = ProductDao()
        var priceDao = PriceDao()
            var queryDao = QueryDao()
                var orderDao = OrderDao()
                    var calendarDayDao = CalendarDayDao()
                        var addressDao = AddressDao()
            private set
companion object {

    @Volatile  private var instance: Database? = null
    fun getInstance(): Database =
        instance ?: synchronized(this){
            instance
                ?:Database().also { instance = it}
        }
}
}