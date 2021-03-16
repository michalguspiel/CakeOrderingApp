package com.erdees.cakeorderingapp.repository

import androidx.lifecycle.LiveData
import com.erdees.cakeorderingapp.dao.PriceDao

class PriceRepository(val dao: PriceDao) {

    fun getPrice():LiveData<List<Double>> = dao.getPrice()

    fun setPrice(priceToSet: Double) = dao.setPrice(priceToSet)

    fun clearPrice() = dao.clearPrice()
}