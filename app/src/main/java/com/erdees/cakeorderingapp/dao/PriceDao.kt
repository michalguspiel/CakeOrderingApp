package com.erdees.cakeorderingapp.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PriceDao {

    private val price: MutableList<Double> = mutableListOf()
    private var priceLive = MutableLiveData<List<Double>>()

    init {
        priceLive.value = price
    }

    fun setPrice(priceToSet: Double){
        price += priceToSet
        priceLive.value = price
    }

    fun getPrice() = priceLive as LiveData<List<Double>>

    fun clearPrice(){
        price.clear()
        priceLive.value = price
    }
}