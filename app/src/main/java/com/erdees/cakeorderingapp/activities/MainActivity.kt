package com.erdees.cakeorderingapp.activities

import android.app.Activity
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
import androidx.fragment.app.FragmentManager

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.SharedPreferences
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.activities.AdminActivity
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate


/**
 * Icons by monkik, Freepik
 * */


class MainActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var cartButton: ImageButton
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuButton: ImageButton
    private lateinit var sideNav: NavigationView
    private lateinit var welcomeTextView: TextView
    private lateinit var footer: Button
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var listOfProductsInBackStack: List<Products>
    private lateinit var sharedPreferences : SharedPreferences

    lateinit var groupedDateList: Map<LocalDate,List<LocalDate>>
    var isUserAdmin = false
    lateinit var snapshotListener: ListenerRegistration


    private val myAccountFragment = MyAccountFragment()
    private val mainFragment = MainFragment()
    private val productsFragment = ProductsFragment()
    private val myCartFragment = MyCartFragment()
    private val myOrdersFragment = MyOrdersFragment()
    private val calendarFragment = CalendarFragment()


fun setLastProductFromTheListToPresentIt(){
    viewModel.setProduct(viewModel.getProductList.value!!.last())
}

    override fun onBackPressed() {
        if(isDeliveryMethodFragmentOpened() == true){
            if(isDeliveryDatePicked()){
            viewModel.clearDate()
            return
        }
            else if(hasUserPickedOccupiedDate()){ //
                restartPickingDate()
                return
            }
        }
        if (sideNav.isVisible) {
            drawerLayout.closeDrawer(Gravity.RIGHT)
            return
        }
        val eachProductFragment = supportFragmentManager.findFragmentByTag(EachProductFragment.TAG)
        if (eachProductFragment != null) {
            if (!viewModel.getProductList.value.isNullOrEmpty() && eachProductFragment.isVisible) {
                setLastProductFromTheListToPresentIt()
                viewModel.removeLastProductFromList() // and remove it from listOfProducts
                return
            }
        }
        if (supportFragmentManager.backStackEntryCount == 1) this.finish()
        else super.onBackPressed()
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "on start")
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    override fun onResume() {
        Log.i(TAG, "on resume")
        super.onResume()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    override fun onDestroy() {
        snapshotListener.remove()
        super.onDestroy()
    }

    private fun updateUI(user: FirebaseUser?) {
        sideNav.menu.findItem(R.id.mi_admin).isVisible = false

        if (user == null) {
            welcomeTextView.text = ""
            footer.visibility = View.VISIBLE
            cartButton.visibility = View.GONE
            sideNav.menu.findItem(R.id.mi_logout).isVisible = false
            return
        }
        else {
            welcomeTextView.text = setWelcomeMsg(user)
            cartButton.visibility = View.VISIBLE
            sideNav.menu.findItem(R.id.mi_logout).isVisible = true
            footer.visibility = View.GONE
            drawerLayout.closeDrawer(Gravity.RIGHT)
            db.collection("users").document(user.uid).get().addOnSuccessListener {
                if(it["admin"] != null && it["admin"] as Boolean){
                    Log.i(TAG,"SET UI FOR ADMIN")
                    sideNav.menu.findItem(R.id.mi_admin).isVisible = true
                }
            }
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
        Log.i(TAG, "ON CREATE MAIN ACTIVITY CALLED")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Firebase.firestore
        auth = Firebase.auth

        sharedPreferences = SharedPreferences(this)
        downloadDeliveryPricesFromServer()
        downloadWaitTimeFromServer()

        /**Get special orders dates
         * If order doesnt contain special product return.
         * Otherwise add date to  date count
         *
         * then group datelist by itself so from calendar day binder app can count size of each group
         * and set color of day square accordingly.
         *
         * Get specialsBooking in order to set calendars inside Application
         * save it in group where key is a Date and values are equivalent to key date, this approach
         * allows to count size of each key value and then set calendar
         * :)*/
        val docRef = db.collection("specialsBooking")
        snapshotListener = docRef.addSnapshotListener { snapShot, error ->
            if (error != null) {
                Log.w("placedOrders", "Listen failed.", error)
                return@addSnapshotListener
            } else {
                val dateList = mutableListOf<LocalDate>()
                docRef.get().addOnSuccessListener { snap ->
                    snap?.forEach { booking ->
                        val date = LocalDate.parse(booking["date"].toString())
                        for (eachTime in 1..(booking["amount"].toString().toInt())) {
                            dateList.add(date)
                        }
                    }
                    groupedDateList = dateList.groupBy { it }
                    viewModel.cleanList() // Clean list before populating it again
                    viewModel.setGroupedDateList(groupedDateList)
                }
            }
        }


        /**Initialize Viewmodel and get products list and last product in that list
         * This allows coming back to previous watched product with back button without changing the whole fragment,
         * just changing data which product is being displayed.*/
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        viewModel.getProductList.observe(this,  { list ->
            listOfProductsInBackStack = list
        })

        val manager: FragmentManager = supportFragmentManager

        /**Bind buttons*/
        cartButton = findViewById(R.id.cart_button)
        menuButton = findViewById(R.id.menu_button)
        footer = findViewById(R.id.footer_item_1)


        /**Functions*/
        fun logout() {
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
            openFragment(myCartFragment, MyCartFragment.TAG, supportFragmentManager,R.id.container)
        }



        /**Side drawer menu */
        drawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.setDrawerLayout(this)

        sideNav = findViewById(R.id.drawer)
        val sideNavListener =
            NavigationView.OnNavigationItemSelectedListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.mi_account -> {
                        if (auth.currentUser == null) openLoginActivity()
                        else openFragment(myAccountFragment, MyAccountFragment.TAG, manager,R.id.container)
                    }
                    R.id.mi_orders -> {
                        if (auth.currentUser == null) openLoginActivity()
                        else openFragment(myOrdersFragment,MyOrdersFragment.TAG,manager,R.id.container)
                    }
                    R.id.mi_admin -> {
                        val adminActivity = Intent(this,AdminActivity::class.java)
                        startActivity(adminActivity,savedInstanceState)
                    }
                    R.id.mi_calendar -> {
                        openFragment(calendarFragment,CalendarFragment.TAG,manager,R.id.container)
                    }
                    R.id.mi_products -> {
                        openFragment(productsFragment, ProductsFragment.TAG, manager,R.id.container)
                    }
                    else -> {
                        logout()
                        item.isVisible = false
                    }
                }
                drawerLayout.closeDrawer(GravityCompat.END)
                return@OnNavigationItemSelectedListener true
            }
        openFragment(mainFragment, MainFragment.TAG, manager,R.id.container)
        welcomeTextView = sideNav.getHeaderView(0).findViewById(R.id.user_name_header)
        sideNav.setNavigationItemSelectedListener(sideNavListener)
    }

    private fun DrawerLayout.setDrawerLayout(activity: Activity){
        val toggle = ActionBarDrawerToggle(activity, this, 0, 0)
        this.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun downloadDeliveryPricesFromServer(){
        val pricesDocRef = db.collection("constants").document("prices")
        pricesDocRef.get().addOnSuccessListener {
            sharedPreferences.save("prePaidCost",it[Constants.prePaidCost].toString())
            sharedPreferences.save("paidAtDeliveryCost",it[Constants.paidAtDeliveryCost].toString())
        }
    }

    private fun downloadWaitTimeFromServer(){
        val waitTimeDocRef = db.collection("constants").document("specialWaitTime")
        waitTimeDocRef.get().addOnSuccessListener {
            sharedPreferences.save("waitTime", it[Constants.waitTime].toString())
        }
    }

    private fun isDeliveryMethodFragmentOpened():Boolean? {
        return supportFragmentManager.findFragmentByTag("DeliveryMethodFragment")?.isVisible
    }

    private fun isDeliveryDatePicked():Boolean {
        return viewModel.getDate.value != null
    }

    private fun hasUserPickedOccupiedDate() : Boolean {
        return viewModel.getOccupiedDate().value != null
    }

    private fun restartPickingDate() = viewModel.setOccupiedDate(null)
}

