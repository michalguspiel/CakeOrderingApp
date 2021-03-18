package com.erdees.cakeorderingapp.model

import com.google.firebase.Timestamp
import java.sql.Time
import java.text.DateFormat
import java.time.Instant
import java.util.*

data class Order(
    val userShoppingCart: List<UserShoppingCart> = listOf(),
    val userId : String = "",
    var deliveryMethod: String = "",
    val paid : Boolean = false,
    val timestamp: Timestamp = Timestamp.now(),
    val orderToBeReadyDate: Timestamp = Timestamp.now(),
    val userAddress: String = "",
    val orderStatus : String = "active",
    val discount : Double = 0.0


) {
}