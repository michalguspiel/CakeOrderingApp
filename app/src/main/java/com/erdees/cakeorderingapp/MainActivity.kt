package com.erdees.cakeorderingapp

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.erdees.cakeorderingapp.adapter.MainActivityRecyclerAdapter
import com.erdees.cakeorderingapp.model.PresentedItem
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Icons by monkik
 * */

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var isUserLogedIn: Boolean = false
    private lateinit var cartButton: ImageButton


    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user == null) {
            Log.i(TAG, "user update ui failed")
            isUserLogedIn = false
            cartButton.visibility = View.GONE
            return
        } else {
            isUserLogedIn = true
            cartButton.visibility = View.VISIBLE
        }

    }


    private companion object {
        const val TAG = "MainActivity"
        const val RC_GOOGLE_SIGN_IN = 7941
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**Get Screen width*/
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        /**Set toolbar*/
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.my_custom_toolbar)
        cartButton = findViewById(R.id.cart_button)

        /**Firebase*/
        auth = Firebase.auth
        val db = Firebase.firestore

        /**Get products to populate main recycler view*/
        val presentationQuery = db.collection("productsForRecycler")
            .orderBy("type", Query.Direction.DESCENDING)
            .limit(10)
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(10)
            .setPageSize(8)
            .build()
        // The options for the adapter combine the paging configuration with query information
        // and application-specific options for lifecycle, etc.
        // The options for the adapter combine the paging configuration with query information
        // and application-specific options for lifecycle, etc.
        val options = FirestorePagingOptions.Builder<PresentedItem>()
            .setLifecycleOwner(this)
            .setQuery(presentationQuery, config, PresentedItem::class.java)
            .build()


        val adapter = MainActivityRecyclerAdapter(this, options, screenWidth)
        val recyclerView = findViewById<RecyclerView>(R.id.mainRecyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)



    }


    private fun setToolbar(menu: Menu?) {
        val logout = menu?.findItem(R.id.mi_logout)
        val login = menu?.findItem(R.id.mi_login)
        if (isUserLogedIn) {
            logout?.isVisible = true
            login?.isVisible = false
        }
        if (!isUserLogedIn) {
            logout?.isVisible = false
            login?.isVisible = true
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        setToolbar(menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        setToolbar(menu)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_logout -> {
                Log.i(TAG, "LOGOUT")
                auth.signOut()
                isUserLogedIn = false
                cartButton.visibility = View.GONE
            }
            R.id.mi_login -> {
                Log.i(TAG, "LOGIN")
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
                auth = Firebase.auth  // Initialize Firebase Auth
                val googleSignInClient = GoogleSignIn.getClient(this, gso)
                // pop dialog with login options. For now just login
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
            }
            else -> Log.i(TAG, "ERROR")
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }


    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                    isUserLogedIn = true
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    // ...
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                    isUserLogedIn = false
                }
            }
    }
}

