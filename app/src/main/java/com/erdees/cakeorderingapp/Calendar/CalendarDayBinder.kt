package androidx.recyclerview.widget.com.erdees.cakeorderingapp.Calendar

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
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

class CalendarDayBinder(val context: Context, val resources: Resources,val isSelectable: Boolean, val viewModel: CalendarDayBinderViewModel,val viewLifeCycleOwner: LifecycleOwner,val containsSpecials: Boolean): DayBinder<DayViewContainer> {

    private val today = LocalDate.now()

    override fun create(view: View) = DayViewContainer(view)

    // Called every time we need to reuse a container.
    override fun bind(container: DayViewContainer, day: CalendarDay) {

        viewModel.getGroupedDateList().observe(viewLifeCycleOwner, Observer { groupedDateList ->
            groupedDateList.forEach{it.value.forEach { value -> Log.i("TEST2",value.toString()) }}
            container.day = day // set calendar day for this container
            /**Check if day exist in grouped date list, if yes check size of group and set color accordingly */
            var color = ""
            color =
                if (groupedDateList[day.date].isNullOrEmpty() || groupedDateList[day.date]?.size!! <= 2) "green"
                else if (groupedDateList[day.date]?.size!! <= 4) "yellow"
                else "gray"


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
                        4
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
             *  - Color of calendar day square must be green or yellow OR order must not contain specials [containsSpecials]
             *  - and day have to be at least 4 days after [today] OR not before today if order doesn't contain specials [containsSpecials]
             *
             *
             * */

            if (isSelectable &&
                (color == "green" || color == "yellow" || !containsSpecials!!) &&
                !day.date.isBefore(today.plusDays(4)) || !containsSpecials && !day.date.isBefore(today)) {
                container.view.setOnClickListener {
                    viewModel?.setDate(day.date)
                }
            }

        })
    }
}