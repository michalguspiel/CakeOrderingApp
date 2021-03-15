package com.erdees.cakeorderingapp.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.erdees.cakeorderingapp.model.Products

class ProductDao {

    private var product: Products? = null
    private val productLive  = MutableLiveData<Products>()

    private var productList: MutableList<Products> = mutableListOf()
    private var productListLive = MutableLiveData<List<Products>>()


    init {
        productLive.value = product
    }
    fun setProduct(productToPresent: Products){
        product = productToPresent
        productLive.value = product
    }

    fun getProduct() = productLive as LiveData<Products>

    fun addProductToList(products: Products) {
        productList.plusAssign(products)
        productListLive.value = productList
    }

    fun removeLastProducts(){
        productList.removeLast()
        productListLive.value = productList
    }

    fun getProductList() = productListLive as LiveData<List<Products>>

    fun getLastProduct() = getProductList().value?.last()

}