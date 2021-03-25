package com.erdees.cakeorderingapp.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.erdees.cakeorderingapp.model.Products

class ProductDao {

    private var product: Products? = null
    private val productLive  = MutableLiveData<Products>()


    /**Product list is a inside backStackList of presented products in eachProductFragment,
     * when user opens suggested product from bottom recycler view
     * just chosen products is set to above productLive and previous product is set as a last one in productListLive.
     *
     * Then when back button is pressed last product from productListLive is set as above productLive and then erased from list.*/
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