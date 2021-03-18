package androidx.recyclerview.widget.com.erdees.cakeorderingapp.repository

import androidx.recyclerview.widget.com.erdees.cakeorderingapp.dao.QueryDao
import com.google.firebase.firestore.Query

class QueryRepository(val dao: QueryDao) {

    fun setViewModelQuery(query: Query) = dao.setViewModelQuery(query)

    fun getViewModelQuery() = dao.getViewModelQuery()
}