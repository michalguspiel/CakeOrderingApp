package androidx.recyclerview.widget.com.erdees.cakeorderingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.repository.CalendarDayRepository
import com.erdees.cakeorderingapp.database.Database

class CalendarFragmentViewModel(application: Application):AndroidViewModel(application) {


    private val calendarDayRepository : CalendarDayRepository

    init {
        val calendarDateDao = Database.getInstance().calendarDayDao
        calendarDayRepository = CalendarDayRepository(calendarDateDao)
    }


    fun getGroupedList() = calendarDayRepository.getGroupedList()
}