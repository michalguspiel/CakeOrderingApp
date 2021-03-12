package com.erdees.cakeorderingapp.model

data class PresentedItem(
        val name: String = "",
        val description: String = "",
        val pictureUrl: String ="",
        val type: Long = 0,
        val picturesArray: List<String> = listOf(),
        val mail: String = "",
        val order: Long = 0
) {
}