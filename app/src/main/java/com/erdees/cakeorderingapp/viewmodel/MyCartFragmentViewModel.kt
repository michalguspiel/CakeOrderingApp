package com.erdees.cakeorderingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.erdees.cakeorderingapp.database.Database
import com.erdees.cakeorderingapp.repository.PriceRepository

class MyCartFragmentViewModel(application: Application): AndroidViewModel(application) {
    private val priceRepository: PriceRepository
    private val getPrice : LiveData<Double>

    init {
        val priceDao = Database.getInstance().priceDao
        priceRepository = PriceRepository(priceDao)
        getPrice = priceRepository.getPrice()
    }

    fun setPrice(number: Double) = priceRepository.setPrice(number)
}

