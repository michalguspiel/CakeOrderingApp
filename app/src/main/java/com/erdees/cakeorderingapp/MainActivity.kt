package com.erdees.cakeorderingapp

import android.accounts.Account
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.erdees.cakeorderingapp.adapter.MainActivityRecyclerAdapter
import com.erdees.cakeorderingapp.model.PresentedItem
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mikepenz.materialdrawer.holder.BadgeStyle
import com.mikepenz.materialdrawer.holder.StringHolder
import com.mikepenz.materialdrawer.model.AbstractDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.nameRes
import com.mikepenz.materialdrawer.model.interfaces.withIdentifier
import com.mikepenz.materialdrawer.util.*
import com.mikepenz.materialdrawer.widget.AccountHeaderView
import com.mikepenz.materialdrawer.widget.MaterialDrawerSliderView
import io.grpc.okhttp.internal.framed.Header

/**
 * Icons by monkik, Freepik
 * */

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var isUserLogedIn: Boolean = false
    private lateinit var cartButton: ImageButton
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuButton: ImageButton
    private lateinit var sideNav: NavigationView
    private lateinit var welcomeTextView : TextView
    private lateinit var footer : Button

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
            sideNav.menu.findItem(R.id.mi_logout).isVisible = false

            return
        } else {
            isUserLogedIn = true
            welcomeTextView.text =  "Welcome " + user.displayName
            cartButton.visibility = View.VISIBLE
            sideNav.menu.findItem(R.id.mi_logout).isVisible = true
            footer.visibility = View.GONE
            drawerLayout.closeDrawer(Gravity.RIGHT)

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

        /**Bind buttons*/
        cartButton = findViewById(R.id.cart_button)
        menuButton = findViewById(R.id.menu_button)
        footer = findViewById<Button>(R.id.footer_item_1)
//        welcomeTextView = findViewById<TextView>(R.id.user_name_header)

        /**Functions
         * */
        fun login() {
            Log.i(TAG, "LOGIN")
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(this, gso)
            auth = Firebase.auth  // Initialize Firebase Auth
            // pop dialog with login options. For now just login
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
        }

        fun logout() {
            Log.i(TAG, "LOGOUT")
            auth.signOut()
            isUserLogedIn = false
            welcomeTextView.text = ""
            footer.visibility = View.VISIBLE
            cartButton.visibility = View.GONE
        }


        /**Buttons functionality*/
        menuButton.setOnClickListener { drawerLayout.openDrawer(Gravity.RIGHT) }

        footer.setOnClickListener {
            login()


        }

        /**Firebase*/
        auth = Firebase.auth
        val db = Firebase.firestore

        /**Get products to populate main recycler view*/
        val presentationQuery = db.collection("productsForRecycler")
            .orderBy("type", Query.Direction.ASCENDING)
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

        /**Setup recycler view*/
        val adapter = MainActivityRecyclerAdapter(this, options, screenWidth)
        val recyclerView = findViewById<RecyclerView>(R.id.mainRecyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        /**Side drawer menu */
        drawerLayout = findViewById(R.id.drawer_layout)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, 0, 0)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        sideNav = findViewById(R.id.drawer)
        val sideNavListener =
            NavigationView.OnNavigationItemSelectedListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.mi_account -> {
                    }
                    R.id.mi_orders -> {
                    }
                    R.id.mi_settings -> {
                    }
                    R.id.mi_calendar -> {
                    }
                    else -> {
                        logout()
                        item.isVisible = false
                    }
                }
                drawerLayout.closeDrawer(GravityCompat.END)
                return@OnNavigationItemSelectedListener true
            }
        welcomeTextView = sideNav.getHeaderView(0).findViewById(R.id.user_name_header)
        sideNav.setNavigationItemSelectedListener(sideNavListener)
    }


    /**
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.toolbar_buttons, menu)
    // setToolbar(menu)
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

     */

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

