package com.erdees.cakeorderingapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.erdees.cakeorderingapp.activities.MainActivity
import com.erdees.cakeorderingapp.model.User
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

class TestActivity: AppCompatActivity() {

    private lateinit var auth: FirebaseAuth


    private companion object {
        const val TAG = "TestActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.another_activity_view)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        auth = Firebase.auth
        // Access a Cloud Firestore instance from your Activity
        val db = Firebase.firestore
        val query = db.collection("users")
        val options = FirestoreRecyclerOptions.Builder<User>().setQuery(query, User::class.java)
        .setLifecycleOwner(this).build()

        val testQuery = db.collection("productsForRecycler")
        testQuery.get()
        .addOnSuccessListener { result ->
        result.forEach { Log.i(TAG,result.documents.toString()) }
        }
        .addOnFailureListener { exception ->
        Log.d(TAG, "Error getting documents: ", exception)
        }

        val adapter = object : FirestoreRecyclerAdapter<User, UserViewHolder>(options){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(this@TestActivity).inflate(android.R.layout.simple_list_item_2,parent,false)
        return UserViewHolder(view)
        }

        override fun onBindViewHolder(holder: UserViewHolder, position: Int, model: User) {
        val text1 = holder.itemView.findViewById<TextView>(android.R.id.text1)
        val text2 = holder.itemView.findViewById<TextView>(android.R.id.text2)
        text1.text = model.name
        }

        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        }
        override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
        R.id.mi_settings -> {
        Log.i(TAG, "LOGOUT")
        auth.signOut()
        val logoutIntent = Intent(this, MainActivity::class.java)
        logoutIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(logoutIntent)
        }
        else -> Log.i(TAG,"ERROR")
        }

        return super.onOptionsItemSelected(item)
        }


}