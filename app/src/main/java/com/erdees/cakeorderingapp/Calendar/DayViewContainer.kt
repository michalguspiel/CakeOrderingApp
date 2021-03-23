package androidx.recyclerview.widget.com.erdees.cakeorderingapp.Calendar

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.erdees.cakeorderingapp.R
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.ui.ViewContainer

class DayViewContainer(view: View): ViewContainer(view) {
    val textView = view.findViewById<TextView>(R.id.calendarDayText)
    val outLayout = view.findViewById<LinearLayout>(R.id.out_layout)
    val inLayout = view.findViewById<LinearLayout>(R.id.in_layout)
    // Will be set when this container is bound
    lateinit var day: CalendarDay

    init {
        view.setOnClickListener {
            // Use the CalendarDay associated with this container.
        }
    }
}