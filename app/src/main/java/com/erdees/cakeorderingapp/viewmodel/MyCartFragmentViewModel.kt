package com.erdees.cakeorderingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.erdees.cakeorderingapp.database.Database
import com.erdees.cakeorderingapp.repository.PriceRepository

class MyCartFragmentViewModel(application: Application): AndroidViewModel(application) {
    private val priceRepository: PriceRepository
    val getPriceList : LiveData<List<Double>>

    init {
        val priceDao = Database.getInstance().priceDao
        priceRepository = PriceRepository(priceDao)
        getPriceList = priceRepository.getPrice()
    }

    fun clearPrice() = priceRepository.clearPrice()
}

