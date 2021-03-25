package androidx.recyclerview.widget.com.erdees.cakeorderingapp.repository

import androidx.recyclerview.widget.com.erdees.cakeorderingapp.dao.AddressDao

class AddressRepository(val dao: AddressDao) {

    fun getAddress() = dao.getAddress()

    fun setAddress(addressToSet: String) = dao.setAddress(addressToSet)



}