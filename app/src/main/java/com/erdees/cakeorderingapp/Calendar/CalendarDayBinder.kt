package androidx.recyclerview.widget.com.erdees.cakeorderingapp.Calendar

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.SharedPreferences
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.viewmodel.CalendarDayBinderViewModel
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.makeInvisible
import com.erdees.cakeorderingapp.makeVisible
import com.erdees.cakeorderingapp.model.Order
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import java.time.LocalDate

class CalendarDayBinder(val context: Context, val resources: Resources,val isSelectable: Boolean, val viewModel: CalendarDayBinderViewModel,val viewLifeCycleOwner: LifecycleOwner): DayBinder<DayViewContainer> {

    private val today = LocalDate.now()

    private val sharedPreferences = SharedPreferences(context)
    private val waitTime = sharedPreferences.getValueString("waitTime")!!.toLong() // !! because there has to be something when this is open

    override fun create(view: View) = DayViewContainer(view)

    // Called every time we need to reuse a container.
    override fun bind(container: DayViewContainer, day: CalendarDay) {

        viewModel.getGroupedDateList().observe(viewLifeCycleOwner, Observer { groupedDateList ->
            container.day = day // set calendar day for this container
            /**Check if day exist in grouped date list, if yes check size of group and set color and booked specials accordingly */
            var bookedSpecials = 0
            var color = ""
            color =
                if (groupedDateList[day.date].isNullOrEmpty() || groupedDateList[day.date]?.size!! <= 2){
                    "green"
                }
                else if(groupedDateList[day.date]?.size!! <= 2 ){
                    bookedSpecials = groupedDateList[day.date]?.size!!
                    "green"
                }
                else if (groupedDateList[day.date]?.size!! <= 4) {
                    bookedSpecials = groupedDateList[day.date]?.size!!
                    "yellow"
                }
                else{
                    bookedSpecials = groupedDateList[day.date]?.size!!
                    "gray"
                }

            val list = listOf(
                container.textView,
                container.inLayout,
                container.outLayout
            )
            container.textView.text = day.date.dayOfMonth.toString()
            if (day.owner != DayOwner.THIS_MONTH) {
                list.forEach { it.makeInvisible() }
            } else if (day.date.isEqual(today)) {
                list.forEach { it.makeVisible() }
                container.textView.setTextColor(Color.WHITE)
                container.outLayout.setBackgroundResource(R.drawable.current_day_background)
                container.inLayout.setBackgroundColor(resources.getColor(R.color.pink_500))
            } else if (!day.date.isEqual(today) && (day.date.isBefore(today) || day.date.isBefore(
                    today.plusDays(
                        waitTime
                    )
                ))
            ) {
                list.forEach { it.makeVisible() }
                container.textView.setTextColor(Color.BLACK)
                container.inLayout.setBackgroundColor(resources.getColor(R.color.gray_100))
                container.outLayout.setBackgroundResource(R.drawable.gray_day_background)
            } else {
                list.forEach { it.makeVisible() }
                when (color) {
                    "green" -> {
                        container.textView.setTextColor(Color.BLACK)
                        container.inLayout.setBackgroundColor(resources.getColor(R.color.green_500))
                        container.outLayout.setBackgroundResource(R.drawable.green_day_background)
                    }

                    "yellow" -> {
                        container.textView.setTextColor(Color.BLACK)
                        container.inLayout.setBackgroundColor(resources.getColor(R.color.yellow_500))
                        container.outLayout.setBackgroundResource(R.drawable.yellow_day_background)
                    }
                    else -> {
                        container.textView.setTextColor(Color.BLACK)
                        container.inLayout.setBackgroundColor(resources.getColor(R.color.gray_100))
                        container.outLayout.setBackgroundResource(R.drawable.gray_day_background)
                    }
                }

            }
            /**To make calendar day selectable
             *  - Calendar must be selectable [isSelectable]
             *  - [specialCount]  must be less or even than available specials for day
             *  - and day have to be at least [waitTime] days after [today] if order contains specials  OR day after today if order doesn't contain specials
             *
             *
             * */
            if(isSelectable) { // If this calendar is selectable check conditions otherwise don't bother
                viewModel.getSpecialCount().observe(viewLifeCycleOwner, Observer { specialCount ->
                    if (specialCount <= (5 - bookedSpecials) &&
                        (!day.date.isBefore(today.plusDays(waitTime)) || specialCount == 0L) &&
                        !day.date.isBefore(today.plusDays(1))
                    ) {
                        container.view.setOnClickListener {
                            viewModel?.setDate(day.date)
                        }
                    } else {
                        container.view.setOnClickListener {
                            if(day.date.isEqual(today)) Toast.makeText(context,"Sorry we don't accept orders for today!",Toast.LENGTH_SHORT).show()
                            else if(day.date.isBefore(today.plusDays(waitTime)))Toast.makeText(context,"Sorry we accept orders with special cakes only $waitTime days ahead due to complicated process.",Toast.LENGTH_SHORT).show()
                            else if (specialCount >= (5 - bookedSpecials)) Toast.makeText(context, "Sorry we are busy this day, we are able to make only ${5 - bookedSpecials} special cakes for this day!", Toast.LENGTH_LONG).show()
                        }
                    }
                })
            }
    })
}
}