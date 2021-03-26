package androidx.recyclerview.widget.com.erdees.cakeorderingapp.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.time.LocalDate
import java.util.*

class CalendarDayDao {

    private var date: LocalDate? = null
    private val dateLive = MutableLiveData<LocalDate>()

    private var occupiedDate: LocalDate? = null
    private val occupiedDateLive = MutableLiveData<LocalDate>()

    private var groupedDateList : Map<LocalDate,List<LocalDate>> = mapOf()
    private val groupedDateListLive = MutableLiveData<Map<LocalDate,List<LocalDate>>>()

    private var presentedDate: LocalDate? = null
    private val presentedDateLive = MutableLiveData<LocalDate>()
    /**Special count which informs calendar how many specials user is trying to order
     * I decided to set it inside calendar day dao because it has direct effect on calendar
     * and there's no point of making another dao*/
    private var specialCount: Long = 0L
    private val specialCountLive =  MutableLiveData<Long>()


    init {
        dateLive.value = date
        occupiedDateLive.value = occupiedDate
        presentedDateLive.value = presentedDate
        groupedDateListLive.value = groupedDateList
        specialCountLive.value = specialCount
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

    fun setSpecialCount(number: Long){
        specialCount = number
        specialCountLive.value = specialCount
    }

    fun getSpecialCount() = specialCountLive as LiveData<Long>

    fun setOccupiedDate(dateToSet: LocalDate?){
        occupiedDate = dateToSet
        occupiedDateLive.value = occupiedDate
    }

    fun getOccupiedDate() = occupiedDateLive as LiveData<LocalDate>


    fun setPresentedDate(dateToSet: LocalDate?){
        presentedDate = dateToSet
        presentedDateLive.value = presentedDate
    }

    fun getPresentedDate() = presentedDateLive as LiveData<LocalDate>

}