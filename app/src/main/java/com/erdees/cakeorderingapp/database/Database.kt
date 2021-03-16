package com.erdees.cakeorderingapp.database

import com.erdees.cakeorderingapp.dao.PriceDao
import com.erdees.cakeorderingapp.dao.ProductDao

class Database private constructor(){

    var productDao = ProductDao()
        var priceDao = PriceDao()
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