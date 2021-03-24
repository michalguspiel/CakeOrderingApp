package com.erdees.cakeorderingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.repository.CalendarDayRepository
import com.erdees.cakeorderingapp.database.Database
import com.erdees.cakeorderingapp.model.Products
import com.erdees.cakeorderingapp.repository.ProductRepository
import java.time.LocalDate

class MainActivityViewModel(application: Application): AndroidViewModel(application) {

    val getProductList: LiveData<List<Products>>

    private val productRepository: ProductRepository
    private val calendarDayRepository : CalendarDayRepository
    val getDate : LiveData<LocalDate>
    init {
        val productDao = Database.getInstance().productDao
        productRepository = ProductRepository(productDao)
        getProductList = productRepository.getProductList()

        val calendarDateDao = Database.getInstance().calendarDayDao
        calendarDayRepository = CalendarDayRepository(calendarDateDao)
        getDate = calendarDayRepository.getDate()
    }

    fun setProduct(product: Products) = productRepository.setProduct(product)

    fun removeLastProductFromList() = productRepository.removeLastProduct()

    fun clearDate() = calendarDayRepository.clearDate()

    fun setGroupedDateList(groupToSet:Map<LocalDate,List<LocalDate>>) = calendarDayRepository.setGroupedList(groupToSet)

    fun cleanList() = calendarDayRepository.cleanList()
}