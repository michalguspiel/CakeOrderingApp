package androidx.recyclerview.widget.com.erdees.cakeorderingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.repository.AddressRepository
import com.erdees.cakeorderingapp.database.Database

class EditAddressDialogViewModel(application: Application): AndroidViewModel(application) {

    val addressRepository: AddressRepository
    val getAddress: LiveData<String>

    init {
        val addressDao = Database.getInstance().addressDao
        addressRepository = AddressRepository(addressDao)
        getAddress = addressRepository.getAddress()
    }

    fun setAddress(addressToSet : String) = addressRepository.setAddress(addressToSet)

}