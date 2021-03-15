package com.erdees.cakeorderingapp.repository

import com.erdees.cakeorderingapp.dao.ProductDao
import com.erdees.cakeorderingapp.model.Products

class ProductRepository(val dao: ProductDao) {

    fun getProduct() = dao.getProduct()

    fun getLastProduct() = dao.getLastProduct()

    fun getProductList() = dao.getProductList()

    fun setProduct(product: Products) = dao.setProduct(product)

    fun addProductToList(product: Products) = dao.addProductToList(product)

    fun removeLastProduct() = dao.removeLastProducts()


}