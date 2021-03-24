package com.erdees.cakeorderingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.repository.CalendarDayRepository
import com.erdees.cakeorderingapp.database.Database
import com.erdees.cakeorderingapp.repository.PriceRepository
import java.time.LocalDate

class DeliveryMethodFragmentViewModel(application: Application): AndroidViewModel(application) {

    private val priceRepository: PriceRepository
    val getPrice : LiveData<Double>

    private val calendarDayRepository : CalendarDayRepository
    val getDate : LiveData<LocalDate>

    init {
        val priceDao = Database.getInstance().priceDao
        priceRepository = PriceRepository(priceDao)
        getPrice = priceRepository.getPrice()

        val calendarDateDao = Database.getInstance().calendarDayDao
        calendarDayRepository = CalendarDayRepository(calendarDateDao)
        getDate = calendarDayRepository.getDate()
    }

    fun cleanDate() = calendarDayRepository.clearDate()

    fun getGroupedDateList() = calendarDayRepository.getGroupedList()
}
