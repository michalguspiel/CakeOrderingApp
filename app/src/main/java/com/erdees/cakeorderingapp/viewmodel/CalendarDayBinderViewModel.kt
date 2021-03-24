package androidx.recyclerview.widget.com.erdees.cakeorderingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.dao.CalendarDayDao
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.repository.CalendarDayRepository
import com.erdees.cakeorderingapp.database.Database
import java.time.LocalDate

class CalendarDayBinderViewModel(application: Application): AndroidViewModel(application) {

    private val calendarDayRepository : CalendarDayRepository
    private val getDate: LiveData<LocalDate>

    init {
        val calendarDayDao = Database.getInstance().calendarDayDao
        calendarDayRepository = CalendarDayRepository(calendarDayDao)
        getDate = calendarDayRepository.getDate()
    }

    fun setDate(dateToSet : LocalDate) = calendarDayRepository.setdate(dateToSet)

    fun getGroupedDateList() = calendarDayRepository.getGroupedList()
}