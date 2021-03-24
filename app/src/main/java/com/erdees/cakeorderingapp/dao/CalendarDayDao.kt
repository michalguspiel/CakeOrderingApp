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

    private var containsSpecial : Boolean = false
    private val containsSpecialLive = MutableLiveData<Boolean>()

    init {
        dateLive.value = date
        groupedDateListLive.value = groupedDateList
        containsSpecialLive.value = containsSpecial
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


    /**Boolean which decides if order has special pastry or no,
     * I decided to set it inside calendar day dao because it has direct effect on calendar
     * and there's no point of making another dao*/
    fun setBoolean(boolean: Boolean) {
        containsSpecial = boolean
        containsSpecialLive.value = containsSpecial
    }

    fun getBoolean() = containsSpecialLive as LiveData<Boolean>

}