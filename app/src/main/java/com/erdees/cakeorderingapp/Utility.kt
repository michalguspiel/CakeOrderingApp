package com.erdees.cakeorderingapp

import android.content.Context
import android.util.DisplayMetrics




open class Utility {


    open fun calculateNoOfColumns(
        context: Context,
        columnWidthDp: Int
    ): Int { // For example columnWidthdp=180
        val displayMetrics: DisplayMetrics = context.getResources().getDisplayMetrics()
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        return (screenWidthDp / columnWidthDp + 0.5).toInt()
    }
}