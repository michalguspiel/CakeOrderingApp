package com.erdees.cakeorderingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.erdees.cakeorderingapp.database.Database
import com.erdees.cakeorderingapp.repository.PriceRepository

class DeliveryMethodFragmentViewModel(application: Application): AndroidViewModel(application) {

    private val priceRepository: PriceRepository
    val getPrice : LiveData<Double>


    init {
        val priceDao = Database.getInstance().priceDao
        priceRepository = PriceRepository(priceDao)
        getPrice = priceRepository.getPrice()
    }

}
