package androidx.recyclerview.widget.com.erdees.cakeorderingapp.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.Query

class QueryDao {

    var query: Query? = null
    val queryLive = MutableLiveData<Query>()

    init {
        queryLive.value = query
    }

    fun setViewModelQuery(queryToSet: Query){
        query = queryToSet
        queryLive.value = query
    }

    fun getViewModelQuery() {
        queryLive as LiveData<Query>
    }

}