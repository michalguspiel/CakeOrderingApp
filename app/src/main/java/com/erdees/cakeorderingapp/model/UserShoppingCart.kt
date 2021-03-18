package com.erdees.cakeorderingapp.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

data class UserShoppingCart(
    @DocumentId val docId: String = "",
    val productId : String = "",
    val userId : String = "",
    val productName : String = "",
    val productPrice : Double = 0.0,
    val quantity : Long = 0,
    val productPictureUrl :String = ""

){
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "productId" to productId,
            "productName" to productName,
            "userId" to userId,
            "productPrice" to productPrice,
            "quantity" to quantity,
            "productPictureUrl" to productPictureUrl,
        )
    }

}
