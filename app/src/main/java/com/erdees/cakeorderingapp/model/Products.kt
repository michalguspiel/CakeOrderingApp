package com.erdees.cakeorderingapp.model

data class Products(
    val productName: String = "",
    val productTags: List<String> = listOf(),
    val productDesc: String = "",
    val productPrice: Double = 0.0,
    val productPictureUrl: String = "",
    val productIngredients: List<String> = listOf(),
    val productWaitTime: Long = 0

) {
}