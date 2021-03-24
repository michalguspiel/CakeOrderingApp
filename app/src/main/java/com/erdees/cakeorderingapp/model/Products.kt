package com.erdees.cakeorderingapp.model

import com.google.firebase.firestore.DocumentId

data class Products(
    @DocumentId val docId: String = "",
    val productName: String = "",
    val productTags: List<String> = listOf(),
    val productDesc: String = "",
    val productPrice: Double = 0.0,
    val productPictureUrl: String = "",
    val productIngredients: List<String> = listOf(),
    val productWaitTime: Long = 0,
    val special: Boolean = false
) {
}