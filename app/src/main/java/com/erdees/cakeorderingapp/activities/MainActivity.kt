package com.erdees.cakeorderingapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import androidx.fragment.app.FragmentTransaction
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.fragments.MainFragment
import com.erdees.cakeorderingapp.fragments.MyAccountFragment
import com.facebook.login.LoginManager

import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


/**
 * Icons by monkik, Freepik
 * */

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var cartButton: ImageButton
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuButton: ImageButton
    private lateinit var sideNav: NavigationView
    private lateinit var welcomeTextView: TextView
    private lateinit var footer: Button

    private val myAccountFragment = MyAccountFragment()
    private val mainFragment = MainFragment()

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "on start")
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    override fun onResume() {
        Log.i(TAG,"on resume")
        super.onResume()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        Log.i(TAG,"UPDATE UI CASTED")
        if (user == null) {
            welcomeTextView.text = ""
            footer.visibility = View.VISIBLE
            Log.i(TAG, "user update ui failed")
            cartButton.visibility = View.GONE
            sideNav.menu.findItem(R.id.mi_logout).isVisible = false
            return
        } else {
            welcomeTextView.text = setWelcomeMsg(user)
            cartButton.visibility = View.VISIBLE
            sideNav.menu.findItem(R.id.mi_logout).isVisible = true
            footer.visibility = View.GONE
            drawerLayout.closeDrawer(Gravity.RIGHT)
        }
    }

private fun setWelcomeMsg(user: FirebaseUser?): String{
    return if (user?.displayName != null ) "Welcome " + user.displayName
    else "Welcome " + user?.email
}

    private companion object {
        const val TAG = "MainActivity"
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
        footer = findViewById(R.id.footer_item_1)


        /**Functions*/
        fun logout() {
            Log.i(TAG, "LOGOUT")
            Firebase.auth.signOut()
            LoginManager.getInstance().logOut()
            updateUI(null)
        }


        fun openFragment(fragment: Fragment, fragmentTag: String) {
            val backStateName = fragment.javaClass.name
            val manager: FragmentManager = supportFragmentManager
            val fragmentPopped = manager.popBackStackImmediate(backStateName, 0)
            if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) { //if fragment isn't in backStack, create it
                val ft: FragmentTransaction = manager.beginTransaction()
                ft.replace(R.id.container, fragment, fragmentTag)
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                ft.addToBackStack(backStateName)
                ft.commit()
            }
        }

        fun openLoginActivity(){
            val loginActivity = Intent(this, LoginActivity::class.java)
            startActivity(loginActivity, savedInstanceState)
        }

        /**Buttons functionality*/
        menuButton.setOnClickListener { drawerLayout.openDrawer(Gravity.RIGHT) }

        footer.setOnClickListener {
            openLoginActivity()
        }

        /**Firebase*/
        auth = Firebase.auth


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
                        if(auth.currentUser == null) openLoginActivity()
                        else openFragment(myAccountFragment,MyAccountFragment.TAG)

                    }
                    R.id.mi_orders -> {
                        if(auth.currentUser == null) openLoginActivity()
                        else Toast.makeText(this,"Orders!",Toast.LENGTH_SHORT).show()
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
        openFragment(mainFragment,MainFragment.TAG)
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


}

