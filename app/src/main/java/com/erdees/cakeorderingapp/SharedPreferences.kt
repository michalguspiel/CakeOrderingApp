package androidx.recyclerview.widget.com.erdees.cakeorderingapp

import android.content.Context
import android.content.SharedPreferences

class SharedPreferences(val context: Context) {
    private val PREF_NAME = "settings"
    private val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE)

    fun save(KEY_NAME: String, text: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY_NAME, text)
        editor.apply()
    }

    fun getValueString(KEY_NAME: String): String? {
        return sharedPref.getString(KEY_NAME, null)
    }

}