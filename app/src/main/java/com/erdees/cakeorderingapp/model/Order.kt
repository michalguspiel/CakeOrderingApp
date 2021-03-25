package com.erdees.cakeorderingapp.model

import androidx.recyclerview.widget.com.erdees.cakeorderingapp.SharedPreferences
import com.google.firebase.Timestamp
import java.sql.Time
import java.text.DateFormat
import java.time.Instant
import java.util.*

data class Order(
    val userShoppingCart: List<UserShoppingCart> = listOf(),
    val userId : String = "",
    var deliveryMethod: String = "",
    var deliveryPrice : Double = 0.0,
    var paid : Boolean = false,
    val timestamp: Timestamp = Timestamp.now(),
    val orderToBeReadyDate: String = "",
    var userAddress: String = "",
    val orderStatus : String = "active",
    val discount : Double = 0.0,
    val specialCount : Long = 0,
    var stripePaymentRef : String = ""
) {
    val priceOfShoppingCart = userShoppingCart.map { it.productPrice * it.quantity }.sum()
    fun totalPriceOfOrder() = priceOfShoppingCart + deliveryPrice
}