package androidx.recyclerview.widget.com.erdees.cakeorderingapp.Calendar

import android.content.res.Resources
import android.view.View
import android.widget.TextView
import androidx.core.view.children
import com.erdees.cakeorderingapp.R
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import java.time.DayOfWeek

class CalendarMonthBinder(val daysOfWeek: Array<DayOfWeek>, val resources: Resources):MonthHeaderFooterBinder<MonthHeaderContainer> {
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