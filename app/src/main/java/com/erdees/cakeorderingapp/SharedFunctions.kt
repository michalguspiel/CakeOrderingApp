package com.erdees.cakeorderingapp

import android.transition.TransitionManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import java.text.NumberFormat
import java.util.*
import kotlin.random.Random

fun randomizeTag():String{
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
       return tagArray[Random(System.currentTimeMillis()).nextInt(tagArray.size-1)]
    }


    fun openFragment(fragment: Fragment, fragmentTag: String, manager: FragmentManager) {
        val backStateName = fragment.javaClass.name
        val fragmentPopped = manager.popBackStackImmediate(backStateName, 0)
        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) { //if fragment isn't in backStack, create it
            val ft: FragmentTransaction = manager.beginTransaction()
            ft.replace(R.id.container, fragment, fragmentTag)
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ft.addToBackStack(backStateName)
            ft.commit()
        }
    }

    fun formatNumber(number: Double) : String {
      return  NumberFormat.getCurrencyInstance(Locale.FRANCE).format(number)
    }



