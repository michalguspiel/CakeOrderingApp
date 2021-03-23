package androidx.recyclerview.widget.com.erdees.cakeorderingapp.Calendar

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.erdees.cakeorderingapp.R
import com.kizitonwose.calendarview.ui.ViewContainer

class MonthHeaderContainer(view: View): ViewContainer(view){
    val textView = view.findViewById<TextView>(R.id.exFourHeaderText)
    val legendLayout = view.findViewById<LinearLayout>(R.id.legendLayout)
}