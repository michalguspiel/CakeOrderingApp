package androidx.recyclerview.widget.com.erdees.cakeorderingapp.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class AddressDao {
    private var address: String? = null
    private val addressLive = MutableLiveData<String>()

    init {
        addressLive.value = address
    }

    fun setAddress(addressToSet: String){
        address = addressToSet
        addressLive.value = address
    }

    fun getAddress() = addressLive as LiveData<String>

}