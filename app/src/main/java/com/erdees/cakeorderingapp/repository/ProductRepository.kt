package com.erdees.cakeorderingapp.repository

import com.erdees.cakeorderingapp.dao.ProductDao
import com.erdees.cakeorderingapp.model.Products

class ProductRepository(val dao: ProductDao) {

    fun getProduct() = dao.getProduct()
    fun setProduct(product: Products) = dao.setProduct(product)

}