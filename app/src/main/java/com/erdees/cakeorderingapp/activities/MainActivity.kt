package com.erdees.cakeorderingapp.activities

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.SharedPreferences
import com.erdees.cakeorderingapp.Constants
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.fragments.*
import com.erdees.cakeorderingapp.model.Products
import com.erdees.cakeorderingapp.openFragment
import com.erdees.cakeorderingapp.viewmodel.MainActivityViewModel
import com.facebook.login.LoginManager

import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.stripe.android.PaymentConfiguration
import com.stripe.android.Stripe


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
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var listOfProductsInBackStack: List<Products>

    private val myAccountFragment = MyAccountFragment()
    private val mainFragment = MainFragment()
    private val productsFragment = ProductsFragment()
    private val myCartFragment = MyCartFragment()

    /** If there are products in viewmodel product list
    set last product from list as main product(presented in eachProductFramgnet)
    then delete that from list
    and return.

    IDK if that's correct approach but when doing in this way
    there's no need to reload fragment,
    and program keeps track of loaded products.

    So productList in productDAO is like backstack of
    presented products in EachProductFragment
     */
    override fun onBackPressed() {
        if (sideNav.isVisible) {
            drawerLayout.closeDrawer(Gravity.RIGHT)
            return
        }
        val eachProductFragment = supportFragmentManager.findFragmentByTag(EachProductFragment.TAG)
        if (eachProductFragment != null) {
            if (!viewModel.getProductList.value.isNullOrEmpty() && eachProductFragment.isVisible) {
                Log.i(TAG, "visible")
                viewModel.setProduct(viewModel.getProductList.value!!.last())
                viewModel.removeLastProductFromList()
                return
            }
        }
        if (supportFragmentManager.backStackEntryCount == 1) this.finish()
        else super.onBackPressed()
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "on start")
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    override fun onResume() {
        Log.i(TAG, "on resume")
        super.onResume()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        Log.i(TAG, "UPDATE UI CASTED")
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

    private fun setWelcomeMsg(user: FirebaseUser?): String {
        return if (user?.displayName != null) "Welcome " + user.displayName
        else "Welcome " + user?.email
    }

    private companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = Firebase.firestore
        /**Shared preferences,
         * whenever app starts
         * delivery pricing is downloaded from server and saved in shared preferences*/
        val sharedPreferences = SharedPreferences(this)

        /**Download correct delivery pricing from server
         * and save it in shared preferences*/
        val pricesDocRef = db.collection("constants").document("prices")
        pricesDocRef.get().addOnSuccessListener {
            sharedPreferences.save("prePaidCost",it[Constants.prePaidCost].toString())
            sharedPreferences.save("paidAtDeliveryCost",it[Constants.paidAtDeliveryCost].toString())
        }

        /**Get Screen width*/
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels


        /**Initialize Viewmodel and get products list and last product in that list*/
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        viewModel.getProductList.observe(this, Observer { list ->
            listOfProductsInBackStack = list
        })


        val manager: FragmentManager = supportFragmentManager

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
            val thisActivityIntent = Intent(this, MainActivity::class.java)
            thisActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(thisActivityIntent)

        }

        fun openLoginActivity() {
            val loginActivity = Intent(this, LoginActivity::class.java)
            startActivity(loginActivity, savedInstanceState)
        }

        /**Buttons functionality*/
        menuButton.setOnClickListener { drawerLayout.openDrawer(Gravity.RIGHT) }

        footer.setOnClickListener {
            openLoginActivity()
        }

        cartButton.setOnClickListener {
            openFragment(myCartFragment, MyCartFragment.TAG, supportFragmentManager)
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
                        if (auth.currentUser == null) openLoginActivity()
                        else openFragment(myAccountFragment, MyAccountFragment.TAG, manager)

                    }
                    R.id.mi_orders -> {
                        if (auth.currentUser == null) openLoginActivity()
                        else Toast.makeText(this, "Orders!", Toast.LENGTH_SHORT).show()
                    }
                    R.id.mi_settings -> {
                    }
                    R.id.mi_calendar -> {
                    }
                    R.id.mi_products -> {
                        openFragment(productsFragment, ProductsFragment.TAG, manager)
                    }
                    else -> {
                        logout()
                        item.isVisible = false
                    }
                }
                drawerLayout.closeDrawer(GravityCompat.END)
                return@OnNavigationItemSelectedListener true
            }
        openFragment(mainFragment, MainFragment.TAG, manager)
        welcomeTextView = sideNav.getHeaderView(0).findViewById(R.id.user_name_header)
        sideNav.setNavigationItemSelectedListener(sideNavListener)
    }


}

