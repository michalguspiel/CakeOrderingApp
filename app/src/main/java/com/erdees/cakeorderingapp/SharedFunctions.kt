package com.erdees.cakeorderingapp

import android.transition.TransitionManager
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.erdees.cakeorderingapp.model.Order
import java.text.NumberFormat
import java.time.DayOfWeek
import java.time.temporal.WeekFields
import java.util.*
import kotlin.random.Random

fun randomizeTag():String{
        val tagArray: Array<String> = Constants().tagArray
       return tagArray[Random(System.currentTimeMillis()).nextInt(tagArray.size-1)]
    }


    fun openFragment(fragment: Fragment, fragmentTag: String, manager: FragmentManager,container: Int ) {
        val backStateName = fragment.javaClass.name
        val fragmentPopped = manager.popBackStackImmediate(backStateName, 0)
        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) { //if fragment isn't in backStack, create it
            val ft: FragmentTransaction = manager.beginTransaction()
            ft.replace(container, fragment, fragmentTag)
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ft.addToBackStack(backStateName)
            ft.commit()
        }
    }

    fun formatNumber(number: Double) : String {
      return  NumberFormat.getCurrencyInstance(Locale.FRANCE).format(number)
    }


    fun View.makeVisible() {
        this.visibility = View.VISIBLE
    }

    fun View.makeInvisible() {
    this.visibility = View.INVISIBLE
}
    fun View.makeGone() {
    this.visibility = View.GONE
}


fun daysOfWeekFromLocale(): Array<DayOfWeek> {
    val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    val daysOfWeek = DayOfWeek.values()
    // Order `daysOfWeek` array so that firstDayOfWeek is at index 0.
    // Only necessary if firstDayOfWeek is not DayOfWeek.MONDAY which has ordinal 0.
    if (firstDayOfWeek != DayOfWeek.MONDAY) {
        val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
        val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
        return  rhs + lhs
    }
    return daysOfWeek
}


fun Order.checkIfContainSpecial():Boolean {
    var result = false
    for (eachItem in this.userShoppingCart) {
        Log.i("TEST check function",eachItem.special.toString())
        Log.i("TEST check function",eachItem.docId)
        result = eachItem.special
        if (result) return true
    }
    return result
}


