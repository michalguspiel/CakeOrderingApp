package androidx.recyclerview.widget.com.erdees.cakeorderingapp.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.time.LocalDate
import java.util.*

class CalendarDayDao {

    private var date: LocalDate? = null
    private val dateLive = MutableLiveData<LocalDate>()

    private var groupedDateList : Map<LocalDate,List<LocalDate>> = mapOf()
    private val groupedDateListLive = MutableLiveData<Map<LocalDate,List<LocalDate>>>()

    init {
        dateLive.value = date
        groupedDateListLive.value = groupedDateList
    }

    fun setDate(dateToSet: LocalDate){
        date = dateToSet
        dateLive.value = date
    }

    fun getDate() = dateLive as LiveData<LocalDate>

    fun clearDate() {
        date = null
        dateLive.value = date
    }

    fun setGroupedDateList(groupToSet : Map<LocalDate,List<LocalDate>>){
        groupedDateList = groupToSet
        groupedDateListLive.value = groupedDateList
    }

    fun getGroupDateList() = groupedDateListLive as LiveData<Map<LocalDate,List<LocalDate>>>

    fun cleanList(){
        groupedDateList = mapOf()
        groupedDateListLive.value = groupedDateList
    }
}