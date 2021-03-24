package com.erdees.cakeorderingapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.Calendar.CalendarDayBinder
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.Calendar.CalendarMonthBinder
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.viewmodel.CalendarDayBinderViewModel
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.viewmodel.CalendarFragmentViewModel
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.activities.MainActivity
import com.erdees.cakeorderingapp.daysOfWeekFromLocale
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

class CalendarFragment: Fragment() {

    private val today = LocalDate.now()
    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.calendar_fragment,container,false)

        /**Setting up a calendar*/
        val calendar = view.findViewById<com.kizitonwose.calendarview.CalendarView>(R.id.delivery_method_calendar)
        val daysOfWeek = daysOfWeekFromLocale()

        val viewModel = ViewModelProvider(this).get(CalendarFragmentViewModel::class.java)


        calendar.dayBinder = CalendarDayBinder(requireContext(),resources,false,ViewModelProvider(this).get(CalendarDayBinderViewModel::class.java),viewLifecycleOwner,false)
        calendar.monthHeaderBinder = CalendarMonthBinder(daysOfWeek,resources)

        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(1)
        val lastMonth = currentMonth.plusMonths(14)
        calendar.setup(firstMonth, lastMonth, daysOfWeek.first())
        calendar.scrollToMonth(currentMonth)




        return view
    }


    companion object {
        const val TAG = "CalendarFragment"
        fun newInstance() : CalendarFragment = CalendarFragment()
    }
}