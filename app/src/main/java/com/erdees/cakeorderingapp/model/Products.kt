package com.erdees.cakeorderingapp.model

data class Products(
    val productName: String = "",
    val productTags: Array<String> = arrayOf(),
    val productDesc: String = "",
    val productPrice: Long = 0,
    val productPictureUrl: String = "",
    val productIngredients: List<String> = listOf(),
    val productWaitTime: Long = 0

) {
}