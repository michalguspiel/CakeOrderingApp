package com.erdees.cakeorderingapp.model

data class UserShoppingCart(
    val productsAndAmount : Map<String, Long> = mapOf()
)
