package com.erdees.cakeorderingapp


class Constants {
    val tagArray: Array<String> = arrayOf(
        "bread",
        "chocolate",
        "dessert",
        "muffin",
        "birthdaycake",
        "eclair",
        "fruits",
        "whitechocolate",
        "donut",
        "cake"
    )

    companion object {
        const val prePaidCost = "prePaidDelivery"
        const val paidAtDeliveryCost = "paidAtDelivery"

        const val waitTime = "waitTime"

        //data about cost of delivery prices which is stored in shared preferences
        // and updated in Main Activity onCreate
        const val sharedPrefPaidDeliveryCost = "prePaidCost"
        const val sharedPrefUnpaidDeliveryCost = "paidAtDeliveryCost"

        const val orderActive = "active"
        const val orderHistory = "completed"

        val timeStampFormat =  java.text.SimpleDateFormat("dd-MM-yyyy HH:mm")


    }
}




