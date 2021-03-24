package androidx.recyclerview.widget.com.erdees.cakeorderingapp.repository

import androidx.recyclerview.widget.com.erdees.cakeorderingapp.dao.CalendarDayDao
import java.time.LocalDate
import java.util.*

class CalendarDayRepository(val dao: CalendarDayDao) {

    fun getDate() = dao.getDate()

    fun setdate(dateToSet: LocalDate) = dao.setDate(dateToSet)

    fun clearDate() = dao.clearDate()

    fun getGroupedList() = dao.getGroupDateList()

    fun setGroupedList(groupToSet : Map<LocalDate,List<LocalDate>> ) = dao.setGroupedDateList(groupToSet)

    fun cleanList() = dao.cleanList()


    /**Booleans
     * */
    fun setBoolean(boolean: Boolean) = dao.setBoolean(boolean)

    fun getBoolean() = dao.getBoolean()
}