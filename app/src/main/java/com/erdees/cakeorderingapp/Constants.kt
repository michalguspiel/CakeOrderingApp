package com.erdees.cakeorderingapp


class Constants {


    companion object {
        const val prePaidCost = "prePaidDelivery"
        const val paidAtDeliveryCost = "paidAtDelivery"

        //data about cost of delivery prices which is stored in shared preferences
        // and updated in Main Activity onCreate
        const val sharedPrefPaidDeliveryCost = "prePaidCost"
        const val sharedPrefUnpaidDeliveryCost = "paidAtDeliveryCost"

        const val orderActive = "active"
        const val orderHistory = "completed"
    }
}




