package com.erdees.cakeorderingapp.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.erdees.cakeorderingapp.model.Products

class ProductDao {

    private var product: Products? = null
    private val productLive  = MutableLiveData<Products>()

    init {
        productLive.value = product
    }
    fun setProduct(productToPresent: Products){
        product = productToPresent
        productLive.value = product
    }

    fun getProduct() = productLive as LiveData<Products>


}