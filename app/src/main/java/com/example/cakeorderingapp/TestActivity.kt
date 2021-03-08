package com.example.cakeorderingapp

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class TestActivity: AppCompatActivity() {

    private lateinit var auth: FirebaseAuth


    private companion object {
        const val TAG = "TestActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.another_activity_view)

        auth = Firebase.auth

    }
        override fun onCreateOptionsMenu(menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.toolbar_menu, menu)
            return true
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_logout -> {
                Log.i(TAG, "LOGOUT")
                auth.signOut()
                val logoutIntent = Intent(this,MainActivity::class.java)
                logoutIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(logoutIntent)
            }
            else -> Log.i(TAG,"ERROR")
        }

        return super.onOptionsItemSelected(item)
    }

}