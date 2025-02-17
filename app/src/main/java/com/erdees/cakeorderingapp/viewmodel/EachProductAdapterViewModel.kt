package com.erdees.cakeorderingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.erdees.cakeorderingapp.database.Database
import com.erdees.cakeorderingapp.model.Products
import com.erdees.cakeorderingapp.repository.ProductRepository

class EachProductAdapterViewModel(application: Application): AndroidViewModel(application) {

    val getProduct: LiveData<Products>
    private val productRepository: ProductRepository

    init {
        val productDao = Database.getInstance().productDao
        productRepository = ProductRepository(productDao)
        getProduct = productRepository.getProduct()
    }

    fun setProduct(product: Products) = productRepository.setProduct(product)

    fun addProductToList(product: Products) = productRepository.addProductToList(product)

}