package com.erdees.cakeorderingapp.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.Calendar.CalendarDayBinder
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.Calendar.CalendarMonthBinder
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.SharedPreferences
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.stripe.FirebaseEphemeralKeyProvider
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.viewmodel.CalendarDayBinderViewModel
import com.erdees.cakeorderingapp.*
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.dialog.fragments.EditAddressDialog
import com.erdees.cakeorderingapp.model.Order
import com.erdees.cakeorderingapp.model.UserShoppingCart
import com.erdees.cakeorderingapp.viewmodel.DeliveryMethodFragmentViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.stripe.android.*
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.PaymentMethod
import com.stripe.android.view.BillingAddressFields
import java.text.NumberFormat
import java.time.LocalDate
import java.time.YearMonth
import java.util.*


class DeliveryMethodFragment : Fragment() {
    private lateinit var user: FirebaseUser
    private lateinit var db: FirebaseFirestore
    private lateinit var userCart: Query
    private lateinit var snapshotListener: ListenerRegistration
    lateinit var paymentSession: PaymentSession
    lateinit var selectedPaymentMethod: PaymentMethod
    private val stripe: Stripe by lazy {
        Stripe(
            requireContext(),
            PaymentConfiguration.getInstance(requireActivity()).publishableKey
        )
    }

    private var costToPay: Double = 0.0
    val today = LocalDate.now()

    private lateinit var confirmPurchaseButton: Button
    private lateinit var paymentMethodTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var loadingText: TextView
    private lateinit var viewModel: DeliveryMethodFragmentViewModel


    private val changeAddressDialog = EditAddressDialog()
        @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.delivery_method_fragment, container, false)
        viewModel = ViewModelProvider(this).get(DeliveryMethodFragmentViewModel::class.java)

        /**Firebase access*/

        val auth = Firebase.auth
        user = auth.currentUser
        db = Firebase.firestore


        setupPaymentSession()

        var userAddress = ""
            var userAnotherAddress = ""
        /**ACCESS TO USER ADDRESS*/
        /**Get user address in order to prevent user from ordering products without providing address*/

        var hasUserProvidedAddress = false

        db.collection("users").document(user.uid).get().addOnSuccessListener { snapshot ->
            val address = snapshot.get("address").toString()
            val address2 = snapshot.get("address2").toString()
            val addressSummed = address + address2 // cause somebody might fill only second field
            val postCode = snapshot.get("postCode").toString()
            val city = snapshot.get("city").toString()

            hasUserProvidedAddress =
                !(addressSummed.isNullOrBlank() || postCode.isNullOrBlank() || city.isNullOrBlank())

            val list = listOf<String>(address, address2, postCode, city)
            userAddress = list.joinToString(" ") { it }
        }

        /**Get delivery prices from shared preferences
         * */
        val sharedPreferences = SharedPreferences(requireContext())
        val prePaidDeliveryCost = sharedPreferences.getValueString("prePaidCost")!!.toDouble()
        val paidAtDeliveryCost = sharedPreferences.getValueString("paidAtDeliveryCost")!!.toDouble()
        val waitTime = sharedPreferences.getValueString("waitTime")!!.toLong()

        /**Setup calendar*/
        val calendar =
            view.findViewById<com.kizitonwose.calendarview.CalendarView>(R.id.delivery_method_calendar)
        val daysOfWeek = daysOfWeekFromLocale()

        /**Creating list of items
         * then getting query of all of this user "userShoppingCarts"
         * and saving it in value userShoppingItems
         * in order to use it when placing order
         *
         * Also check if each object is special, if so add quantity to specialCount in order to create a booking*/
        var specialCount = 0L
        val userShoppingItems: MutableList<UserShoppingCart> = mutableListOf()
        userCart = db.collection("userShoppingCart").whereEqualTo("userId", user.uid)
        userCart.get().addOnSuccessListener { snapshot ->
            snapshot.forEach {
                val itemAsObject = it.toObject<UserShoppingCart>()
                userShoppingItems += itemAsObject
                if (itemAsObject.special) {
                    specialCount += itemAsObject.quantity
                }
            }
            viewModel.setSpecialCount(specialCount)
        }


        calendar.dayBinder = CalendarDayBinder(
            requireContext(), resources, true, ViewModelProvider(
                this
            ).get(CalendarDayBinderViewModel::class.java),
            viewLifecycleOwner
        )
        calendar.monthHeaderBinder = CalendarMonthBinder(daysOfWeek, resources)
        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(1)
        val lastMonth =
            currentMonth.plusMonths(6) // no need to make it more that half an year upfront
        calendar.setup(firstMonth, lastMonth, daysOfWeek.first())
        calendar.scrollToMonth(currentMonth)

        /**Binders*/
        val costParenthesis = view.findViewById<TextView>(R.id.delivery_method_cost_plus_delivery)
        val totalCost = view.findViewById<TextView>(R.id.delivery_method_total_cost)
        val pickedDateTextView = view.findViewById<TextView>(R.id.delivery_picked_date_text)
        val radioGroup = view.findViewById<RadioGroup>(R.id.delivery_method_group)
        val radioButtonPickup = view.findViewById<RadioButton>(R.id.delivery_pickup)
        val radioButtonNotPaid = view.findViewById<RadioButton>(R.id.delivery_elves_notpaid)
        val radioButtonPaid = view.findViewById<RadioButton>(R.id.delivery_elves_upfront)
        val scrollView = view.findViewById<ScrollView>(R.id.delivery_scroll_view)
        val pickedDateInfoText = view.findViewById<TextView>(R.id.delivery_picked_date_information)
        val addressTextView = view.findViewById<TextView>(R.id.delivery_address_text)
        val changeAddressButton = view.findViewById<TextView>(R.id.delivery_change_address)
        val useHomeAddressButton = view.findViewById<TextView>(R.id.delivery_use_home_address)

        /**Getting screen width and height and then set calendarHolderLayout as it
         * so when user picks date for order screen gets full scroll and shows exacly what's expected on every device*/
        val calendarHolderLayout = view.findViewById<LinearLayout>(R.id.linear_calendar_holder)
        val calendarHolderLayout2 =
            view.findViewById<ConstraintLayout>(R.id.contraint_layout_holder)
        val displayMetrics = context!!.resources.displayMetrics

        calendarHolderLayout.layoutParams =
            LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels)
        calendarHolderLayout2.layoutParams =
            LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels)

        calendarHolderLayout.requestLayout()
        calendarHolderLayout2.requestLayout()
        /**Disabled scroll because actions cause scrolling*/
        val disabledScroll = OnTouchListener() { _: View?, _: MotionEvent? ->
            return@OnTouchListener true
        }
        scrollView.setOnTouchListener(disabledScroll)

        confirmPurchaseButton = view.findViewById<Button>(R.id.delivery_method_confirm_button)
        paymentMethodTextView = view.findViewById(R.id.payment_method_text_view)
        progressBar = view.findViewById(R.id.progress_bar)
        loadingText = view.findViewById(R.id.loading_text_view)
        fun format(number: Double): String {
            return NumberFormat.getCurrencyInstance(Locale.FRANCE).format(number)
        }

        val cartCost = viewModel.getPrice.value!! // TO GET CART COST FROM VIEWMODEL & CART FRAGMENT
        paymentMethodTextView.visibility = View.GONE


        var groupedDateList: Map<LocalDate, List<LocalDate>> = mapOf()
        viewModel.getGroupedDateList().observe(viewLifecycleOwner, { groupedList ->
            groupedDateList = groupedList
        })

        /**Getting date picked by user which is already too busy to take this order*/
        viewModel.getOccupiedDate().observe(viewLifecycleOwner, { date ->
            if (date != null) {
                if (date.isEqual(today)) pickedDateInfoText.text =
                    "Sorry we don't accept orders for today."
                else if (date.isBefore(today.plusDays(waitTime))) pickedDateInfoText.text =
                    "Sorry your order contains special product, we need to get this order one day in advance to make it happen."
                else {
                    val howManySpecials = groupedDateList[date]?.size
                    val howManySpecialsLeft = 5 - howManySpecials!!.toInt()
                    if (howManySpecialsLeft == 0) pickedDateInfoText.text =
                        "Sorry we can't make anymore specials on $date."
                    else if(howManySpecialsLeft == 1) pickedDateInfoText.text = "Sorry there's only one special left on $date."
                    else  pickedDateInfoText.text =
                        "Sorry we can't take more than ${5 - howManySpecials!!.toInt()} specials for $date. "
                }
            } else pickedDateInfoText.text = ""
        })

        /**Getting date picked by user from viewmodel and setting it as textview*/
        var pickedDate: LocalDate? = null
        viewModel.getDate.observe(viewLifecycleOwner, { date ->
            if (date != null) {
                scrollView.fullScroll(View.FOCUS_DOWN)
                pickedDateTextView.text = "Order for: $date."
                pickedDate = date
                Log.i(TAG, LocalDate.parse(pickedDate.toString()).toString())
            } else {
                scrollView.fullScroll(View.FOCUS_UP)
                pickedDateTextView.text = ""
                pickedDate = null
            }
        })

        /**Get address from viewModel
         * in case user want to place order for not his address.*/
        viewModel.getAddress.observe(viewLifecycleOwner, { address ->
            if (address.isNullOrBlank()){
                userAnotherAddress = ""
            }
            else if (!address.isNullOrBlank()) {
                userAnotherAddress = address
            }
            if(userAnotherAddress.isNullOrBlank()) {
                addressTextView.text = setAddress(userAddress)
                changeAddressButton.makeVisible()
                useHomeAddressButton.makeGone()
            }
            else {
                addressTextView.text = setAddress(userAnotherAddress)
                changeAddressButton.makeGone()
                useHomeAddressButton.makeVisible()
            }
        })


        val radioListener = RadioGroup.OnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.delivery_pickup -> {
                    paymentMethodTextView.makeGone()
                    addressTextView.makeInvisible()
                    changeAddressButton.makeGone()
                    useHomeAddressButton.makeGone()
                    confirmPurchaseButton.isEnabled = true
                    costToPay = cartCost
                    totalCost.text = format(costToPay)
                    costParenthesis.text = ""
                }
                R.id.delivery_elves_notpaid -> {
                    addressTextView.makeVisible()
                    if(userAnotherAddress.isNullOrBlank()){
                        addressTextView.text = setAddress(userAddress)
                        changeAddressButton.makeVisible()
                        useHomeAddressButton.makeGone()
                    } else {
                        addressTextView.text = setAddress(userAnotherAddress)
                        changeAddressButton.makeGone()
                        useHomeAddressButton.makeVisible()
                    }
                    paymentMethodTextView.makeGone()
                    confirmPurchaseButton.isEnabled = true
                    userShoppingItems.forEach { Log.i(TAG, it.productName + it.quantity) }
                    costToPay = cartCost + paidAtDeliveryCost
                    totalCost.text = format(costToPay)
                    costParenthesis.text = "(${format(cartCost)} + ${format(paidAtDeliveryCost)})"
                }
                R.id.delivery_elves_upfront -> {
                    addressTextView.makeVisible()
                    if(userAnotherAddress.isNullOrBlank()){
                        addressTextView.text = setAddress(userAddress)
                        changeAddressButton.makeVisible()
                        useHomeAddressButton.makeGone()
                    } else {
                        addressTextView.text = setAddress(userAnotherAddress)
                        changeAddressButton.makeGone()
                        useHomeAddressButton.makeVisible()
                    }
                    costToPay = cartCost + prePaidDeliveryCost
                    paymentMethodTextView.makeVisible()
                    confirmPurchaseButton.isEnabled = false

                    setupPaymentSession()
                    totalCost.text = format(costToPay)
                    costParenthesis.text = "(${format(cartCost)} + ${format(prePaidDeliveryCost)})"

                }
                else -> {
                    Toast.makeText(requireContext(), "ERRR", Toast.LENGTH_SHORT).show()
                }
            }
        }
        radioGroup.setOnCheckedChangeListener(radioListener)
        radioGroup.check(R.id.delivery_pickup) // to start with first button checked


        changeAddressButton.setOnClickListener {
            changeAddressDialog.show(parentFragmentManager, TAG)
        }

         useHomeAddressButton.setOnClickListener {
             viewModel.setAddress("")
             useHomeAddressButton.makeGone()
             changeAddressButton.makeVisible()
         }




        confirmPurchaseButton.setOnClickListener {


            val orderToPlace: Order = Order(
                userShoppingItems,
                user.uid,
                "Pickup",
                0.0,
                false,
                Timestamp.now(),
                pickedDate.toString(),
                userAddress,
                Constants.orderActive,
                0.0,
                specialCount
            )
            if (userAnotherAddress.isNotBlank()) orderToPlace.userAddress = userAnotherAddress

            when { // FIRST CHANGING ORDER VALUES TO APPROPRIATE THEN PLACING ORDER THEN  SHOW CONFIRMATION DIALOG WHICH TRIGGERS INFORMATION DIALOG
                radioButtonPickup.isChecked -> {
                    orderToPlace.deliveryMethod = "Pickup"
                    orderToPlace.deliveryPrice = 0.0
                    orderToPlace.paid = false
                    showDialogDoubleConfirmation(orderToPlace)

                }
                radioButtonNotPaid.isChecked -> {
                    if (!hasUserProvidedAddress) {
                        showAddressWarningDialog()
                        return@setOnClickListener
                    }
                    orderToPlace.deliveryPrice = paidAtDeliveryCost
                    orderToPlace.deliveryMethod = "Delivery unpaid"
                    orderToPlace.paid = false
                    showDialogDoubleConfirmation(orderToPlace)
                }
                radioButtonPaid.isChecked -> {
                    if (!hasUserProvidedAddress) {
                        showAddressWarningDialog()
                        return@setOnClickListener
                    }
                    orderToPlace.deliveryPrice = prePaidDeliveryCost
                    orderToPlace.deliveryMethod = "Delivery prepaid"
                    orderToPlace.paid = true
                    confirmPayment(selectedPaymentMethod.id!!, orderToPlace)
                }
            }

        }
        paymentMethodTextView.setOnClickListener {
            paymentSession.presentPaymentMethodSelection()
        }


        setupPaymentSession()
            radioGroup.check(R.id.delivery_pickup) // to start with first button checked

            return view
    }

    private fun setupPaymentSession() {
        PaymentConfiguration.init(
            requireContext(),
            "pk_test_51IWNZYFWPZNf15isNRfq1xDAm8IrX0Rs4AHQJWkzpHiUPmtjB2gubJFlkxUi8v3QN1DvrYFztoyYI67khp499F7y005giE0ss6"
        )
        // Setup Customer Session
        CustomerSession.initCustomerSession(requireContext(), FirebaseEphemeralKeyProvider())
        // Setup a payment session
        paymentSession = PaymentSession(
            this, PaymentSessionConfig.Builder()
                .setShippingInfoRequired(false)
                .setShippingMethodsRequired(false)
                .setBillingAddressFields(BillingAddressFields.None)
                .setShouldShowGooglePay(false)
                .build()
        )

        paymentSession.init(
            object : PaymentSession.PaymentSessionListener {
                override fun onPaymentSessionDataChanged(data: PaymentSessionData) {
                    Log.d("PaymentSession", "PaymentSession has changed: $data")
                    Log.d(
                        "PaymentSession",
                        "${data.isPaymentReadyToCharge} <> ${data.paymentMethod}"
                    )

                    if (data.isPaymentReadyToCharge) {
                        Log.d("PaymentSession", "Ready to charge");
                        confirmPurchaseButton.isEnabled = true

                        data.paymentMethod?.let {
                            Log.d("PaymentSession", "PaymentMethod $it selected")
                            paymentMethodTextView.text =
                                "${it.card?.brand}  -${it.card?.last4}"
                            selectedPaymentMethod = it
                        }
                    }
                }

                override fun onCommunicatingStateChanged(isCommunicating: Boolean) {
                    Log.d("PaymentSession", "isCommunicating $isCommunicating")
                }

                override fun onError(errorCode: Int, errorMessage: String) {
                    Log.e("PaymentSession", "onError: $errorCode, $errorMessage")
                }
            }
        )

    }



    /**FUNCTIONS PLACE ORDER AND DELETE EVERY ITEM FROM USERSHOPPINGCART*/

    fun setAddress(address: String?):String{
        return if(address.isNullOrBlank()) "You haven't provided address in your profile."
        else "Delivery address: $address."
    }

    fun placeOrder(order: Order) {
        db.collection("placedOrders").document().set(order)
    }

    fun cleanShoppingCart() {
        userCart.get().addOnSuccessListener { snapShot ->
            snapShot.forEach {
                db.collection("userShoppingCart").document(it.id).delete()
            }
        }
    }

    fun cleanPickedDate() {
        viewModel.cleanDate()

    }

    /**INFORMATION DIALOG WHICH INFORMS IF ORDER WAS PLACED SUCCESFULLY
     * IN CASE PARAMETER BOOLEAN WAS TRUE SNAPSHOTLISTENER GETS REMOVED*/
    fun showDialog(deleteSnapshot: Boolean) {
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage("Your order has been placed successfully.")
            .setOnDismissListener { // if true remove snapshot
                if (deleteSnapshot) snapshotListener.remove()
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }

            .setNegativeButton("Continue", null)
            .show()
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
            dialog.dismiss()
        }
    }

    /**WHILE ORDERING WITHOUT PRE PAYING APP SHOWS CONFIRMATION DIALOG
     * WHICH MOVES PROCESS FORWARD*/
    fun showDialogDoubleConfirmation(order: Order) {
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage("I order with an obligation to pay")
            .setPositiveButton("Proceed", null)
            .setNegativeButton("Cancel", null)
            .show()

        dialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener {
            placeOrder(order)
            cleanShoppingCart()
            cleanPickedDate()
            showDialog(false)
            dialog.dismiss()
        }

    }

    fun showAddressWarningDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("You must provide address for your account")
            .setNegativeButton("Back", null)
            .show()
    }

    fun startLoading() {
        progressBar.visibility = View.VISIBLE
        loadingText.visibility = View.VISIBLE
    }

    fun stopLoading() {
        progressBar.visibility = View.GONE
        loadingText.visibility = View.GONE
    }

    private fun confirmPayment(paymentMethodId: String, order: Order) {
        confirmPurchaseButton.isEnabled = false
        startLoading()

        val paymentCollection = Firebase.firestore
            .collection("stripe_customers").document(user?.uid ?: "")
            .collection("payments")


        // Add a new document with a generated ID

        paymentCollection.add(
            hashMapOf(
                "amount" to (costToPay * 100).toLong(),
                "currency" to "EUR"
            )
        )
            .addOnSuccessListener { documentReference ->
                Log.d("payment", "DocumentSnapshot added with ID: ${documentReference.id}")
                snapshotListener = documentReference.addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w("payment", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d("payment", "Current data: ${snapshot.data}")
                        val clientSecret = snapshot.data?.get("client_secret")
                        Log.d("payment", "Create paymentIntent returns $clientSecret")
                        clientSecret?.let {
                            stripe.confirmPayment(
                                this, ConfirmPaymentIntentParams.createWithPaymentMethodId(
                                    paymentMethodId,
                                    (it as String)
                                )
                            )
                            order.stripePaymentRef = snapshot.id
                            placeOrder(order)
                            cleanShoppingCart()
                            cleanPickedDate()
                            viewModel.cleanDate()
                            stopLoading()
                            progressBar.visibility = View.GONE
                            showDialog(true)

                        }
                    } else {
                        Log.e("payment", "Current payment intent : null")
                        confirmPurchaseButton.isEnabled = true
                    }
                }
            }

            .addOnFailureListener { e ->
                Log.w("payment", "Error adding document", e)
                confirmPurchaseButton.isEnabled = true
            }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            paymentSession.handlePaymentData(requestCode, resultCode, data)
        }
    }

    companion object {
        const val TAG = "DeliveryMethodFragment"
        fun newInstance(): DeliveryMethodFragment = DeliveryMethodFragment()
    }
}

