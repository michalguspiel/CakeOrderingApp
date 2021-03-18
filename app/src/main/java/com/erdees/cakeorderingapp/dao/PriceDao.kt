package com.erdees.cakeorderingapp.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PriceDao {

    private var price: Double = 0.0
    private var priceLive = MutableLiveData<Double>()

    init {
        priceLive.value = price
    }

    fun setPrice(priceToSet: Double){
        price = priceToSet
        priceLive.value = price
    }

    fun getPrice() = priceLive as LiveData<Double>

}