package com.erdees.cakeorderingapp.model

import com.google.firebase.Timestamp

data class Order(
    val productAndAmount: Map<String,Long> = mapOf(),
    val userRef : String = "",
    val date: Timestamp = Timestamp.now(),
    val orderStatus:String = "active"

) {
}