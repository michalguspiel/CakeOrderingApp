package com.erdees.cakeorderingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.repository.QueryRepository
import com.erdees.cakeorderingapp.database.Database
import com.erdees.cakeorderingapp.repository.PriceRepository
import com.google.firebase.firestore.Query

class MyCartFragmentViewModel(application: Application): AndroidViewModel(application) {
    private val priceRepository: PriceRepository
    private val queryRepository : QueryRepository

    private val getPrice : LiveData<Double>


    init {
        val priceDao = Database.getInstance().priceDao
        val queryDao = Database.getInstance().queryDao
        priceRepository = PriceRepository(priceDao)
        queryRepository = QueryRepository(queryDao)
        getPrice = priceRepository.getPrice()
    }
    /**Price methods*/
    fun setPrice(number: Double) = priceRepository.setPrice(number)

    /**Query methods*/
    fun setViewModelQuery(queryToSet: Query) = queryRepository.setViewModelQuery(queryToSet)

}

