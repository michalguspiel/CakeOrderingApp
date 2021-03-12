package com.erdees.cakeorderingapp.model

data class User(
    val name: String = "",
    val address: String = "",
    val address2: String = "",
    val postCode: String = "",
    val city: String = "",
    val points : Int = 0 // loyalty points
) {
}