package androidx.recyclerview.widget.com.erdees.cakeorderingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.repository.CalendarDayRepository
import com.erdees.cakeorderingapp.database.Database
import java.time.LocalDate

class AdminOrdersFragmentViewModel(application: Application): AndroidViewModel(application) {

    val calendarDayRepository :  CalendarDayRepository
    val getPresentedDate : LiveData<LocalDate>

    init {
        val dao = Database.getInstance().calendarDayDao
        calendarDayRepository = CalendarDayRepository(dao)
        getPresentedDate = calendarDayRepository.getPresentedDate()
    }

    fun setPresentedDate(dateToSet: LocalDate) = calendarDayRepository.setPresentedDate(dateToSet)

}