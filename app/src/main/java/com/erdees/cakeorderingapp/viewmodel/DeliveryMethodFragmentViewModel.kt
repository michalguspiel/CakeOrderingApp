package com.erdees.cakeorderingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.repository.AddressRepository
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.repository.CalendarDayRepository
import com.erdees.cakeorderingapp.database.Database
import com.erdees.cakeorderingapp.repository.PriceRepository
import java.time.LocalDate

class DeliveryMethodFragmentViewModel(application: Application): AndroidViewModel(application) {

    private val priceRepository: PriceRepository
    val getPrice : LiveData<Double>

    private val calendarDayRepository : CalendarDayRepository
    val getDate : LiveData<LocalDate>

    private val addressRepository : AddressRepository
    val getAddress : LiveData<String>
    init {
        val priceDao = Database.getInstance().priceDao
        priceRepository = PriceRepository(priceDao)
        getPrice = priceRepository.getPrice()

        val calendarDateDao = Database.getInstance().calendarDayDao
        calendarDayRepository = CalendarDayRepository(calendarDateDao)
        getDate = calendarDayRepository.getDate()

        val addressDao = Database.getInstance().addressDao
        addressRepository = AddressRepository(addressDao)
        getAddress = addressRepository.getAddress()
    }

    fun cleanDate() = calendarDayRepository.clearDate()

    fun setSpecialCount(number: Long) = calendarDayRepository.setSpecialCount(number)

    fun getOccupiedDate() = calendarDayRepository.getOccupiedDate()

    fun getGroupedDateList() = calendarDayRepository.getGroupedList()

    fun setAddress(addressToSet: String) = addressRepository.setAddress(addressToSet)

}
