package com.erdees.cakeorderingapp.viewmodel

import android.app.Application
import android.provider.ContactsContract
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.erdees.cakeorderingapp.database.Database
import com.erdees.cakeorderingapp.model.Products
import com.erdees.cakeorderingapp.repository.ProductRepository

class EachProductFragmentViewModel(application: Application): AndroidViewModel(application) {

    val getProduct: LiveData<Products>
    private val productRepository: ProductRepository

    init {
        val productDao = Database.getInstance().productDao
        productRepository = ProductRepository(productDao)
        getProduct = productRepository.getProduct()
    }

}