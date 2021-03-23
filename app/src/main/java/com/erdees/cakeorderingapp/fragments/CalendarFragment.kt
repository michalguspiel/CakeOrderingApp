package com.erdees.cakeorderingapp.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.Calendar.DayViewContainer
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.Calendar.MonthHeaderContainer
import com.erdees.cakeorderingapp.R
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*

class CalendarFragment: Fragment() {

    private val selectedDates = mutableSetOf<LocalDate>()
    private val today = LocalDate.now()
    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.calendar_fragment,container,false)

        /**Setting up a calendar*/
        val calendar = view.findViewById<com.kizitonwose.calendarview.CalendarView>(R.id.calendar_view)

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

        val daysOfWeek = daysOfWeekFromLocale()

        calendar.dayBinder = object : DayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                container.textView.text = day.date.dayOfMonth.toString()
                if(day.date.isEqual(today)) container.textView.setTextColor(Color.WHITE)
                else if(day.date.isBefore(today) || day.date.isBefore(today.plusDays(4))){
                    container.inLayout.setBackgroundColor(resources.getColor(R.color.gray_100))
                    container.outLayout.setBackgroundResource(R.drawable.gray_day_background)
                }
                else{
                    container.inLayout.setBackgroundColor(resources.getColor(R.color.green_500))
                    container.outLayout.setBackgroundResource(R.drawable.green_day_background)
                }
            }
        }
        // TODO FIX BUGS AND FINISH 
        calendar.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthHeaderContainer>{
            override fun bind(container: MonthHeaderContainer, month: CalendarMonth) {
                container.textView.text =
                    "${month.yearMonth.month.name.toLowerCase().capitalize()} ${month.year}"
                    container.legendLayout.children.map { it as TextView }
                        .forEachIndexed { index, tv ->
                            tv.text = daysOfWeek[index].name.take(3)
                            tv.setTextColor(resources.getColor(R.color.black))
                        }

                }


            override fun create(view: View): MonthHeaderContainer {
              return  MonthHeaderContainer(view)
            }

        }



        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(1)
        val lastMonth = currentMonth.plusMonths(6)
        Log.i(TAG,daysOfWeek.first().toString())
        calendar.setup(firstMonth, lastMonth, daysOfWeek.first())
        calendar.scrollToMonth(currentMonth)




        return view
    }


    companion object {
        const val TAG = "CalendarFragment"
        fun newInstance() : CalendarFragment = CalendarFragment()
    }
}