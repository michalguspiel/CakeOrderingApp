package com.erdees.cakeorderingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.erdees.cakeorderingapp.database.Database
import com.erdees.cakeorderingapp.model.Products
import com.erdees.cakeorderingapp.repository.PriceRepository
import com.erdees.cakeorderingapp.repository.ProductRepository

class CartItemsRecyclerAdapterViewModel(application: Application): AndroidViewModel(application) {
    //private val getPrice: LiveData<List<Double>>
    private val priceRepository: PriceRepository
    private val getPriceList : LiveData<List<Double>>

    init {
        val priceDao = Database.getInstance().priceDao
        priceRepository = PriceRepository(priceDao)
        //getPrice = priceRepository.getPrice()
        getPriceList = priceRepository.getPrice()
    }

    fun setPrice(priceToSet: Double) = priceRepository.setPrice(priceToSet)

    fun clearPrice() = priceRepository.clearPrice()
}

