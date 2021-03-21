package com.erdees.cakeorderingapp.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.SharedPreferences
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.stripe.FirebaseEphemeralKeyProvider
import com.erdees.cakeorderingapp.R
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
    private lateinit var confirmPurchaseButton: Button
    private lateinit var paymentMethodTextView: TextView
    private lateinit var progressBar : ProgressBar
    private lateinit var loadingText : TextView

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.delivery_method_fragment, container, false)
        val viewModel = ViewModelProvider(this).get(DeliveryMethodFragmentViewModel::class.java)

        /**Firebase access*/

        val auth = Firebase.auth
        user = auth.currentUser
        db = Firebase.firestore


        setupPaymentSession()

        var userAddress = ""
        /**ACCESS TO USER ADDRESS*/
        /**Get user address in order to prevent user from ordering products without providing address*/

        var hasUserProvidedAddress: Boolean = false

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



        /**Binders*/
        val costParenthesis      = view.findViewById<TextView>(R.id.delivery_method_cost_plus_delivery)
        val totalCost            = view.findViewById<TextView>(R.id.delivery_method_total_cost)
        val radioGroup           = view.findViewById<RadioGroup>(R.id.delivery_method_group)
        val radioButtonPickup    = view.findViewById<RadioButton>(R.id.delivery_pickup)
        val radioButtonNotPaid   = view.findViewById<RadioButton>(R.id.delivery_elves_notpaid)
        val radioButtonPaid      = view.findViewById<RadioButton>(R.id.delivery_elves_upfront)
        confirmPurchaseButton    = view.findViewById<Button>(R.id.delivery_method_confirm_button)
        paymentMethodTextView    = view.findViewById(R.id.payment_method_text_view)
        progressBar              = view.findViewById(R.id.progress_bar)
        loadingText              = view.findViewById(R.id.loading_text_view)
        fun format(number: Double): String {
            return NumberFormat.getCurrencyInstance(Locale.FRANCE).format(number)
        }

        val cartCost = viewModel.getPrice.value!! // TO GET CART COST FROM VIEWMODEL & CART FRAGMENT
        paymentMethodTextView.visibility = View.GONE
        /**Creating list of items
         * then getting query of all of this user "userShoppingCarts"
         * and saving it in value userShoppingItems
         * in order to use it when placing order*/
        val userShoppingItems: MutableList<UserShoppingCart> = mutableListOf()
        userCart = db.collection("userShoppingCart").whereEqualTo("userId", user.uid)
        userCart.get().addOnSuccessListener { snapshot ->
            snapshot.forEach { userShoppingItems += it.toObject<UserShoppingCart>() }
        }
        userShoppingItems.forEach { Log.i(TAG, it.productName + it.quantity) }


        val radioListener = RadioGroup.OnCheckedChangeListener { group, checkedId ->

            when (checkedId) {
                R.id.delivery_pickup -> {
                    paymentMethodTextView.visibility = View.GONE
                    confirmPurchaseButton.isEnabled = true
                    costToPay = cartCost
                    totalCost.text = format(costToPay)
                    costParenthesis.text = ""
                    Toast.makeText(requireContext(), "pickup", Toast.LENGTH_SHORT).show()
                }
                R.id.delivery_elves_notpaid -> {
                    paymentMethodTextView.visibility = View.GONE

                    confirmPurchaseButton.isEnabled = true
                    userShoppingItems.forEach { Log.i(TAG, it.productName + it.quantity) }
                    costToPay = cartCost + paidAtDeliveryCost
                    totalCost.text = format(costToPay)
                    costParenthesis.text = "(${format(cartCost)} + ${format(paidAtDeliveryCost)})"
                    Toast.makeText(requireContext(), "notpaid", Toast.LENGTH_SHORT).show()
                }
                R.id.delivery_elves_upfront -> {
                    costToPay = cartCost + prePaidDeliveryCost
                    paymentMethodTextView.visibility = View.VISIBLE
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


        confirmPurchaseButton.setOnClickListener {

            val orderToPlace: Order = Order(
                userShoppingItems,
                user.uid,
                "Pickup",
                false,
                Timestamp.now(),
                Timestamp.now(), // TODO in future picking up date
                userAddress,
                "active",
                0.0
            )

            when { // FIRST CHANGING ORDER VALUES TO APPROPRIATE THEN SHOW CONFIRMATION DIALOG WHICH TRIGGERS INFORMATION DIALOG
                radioButtonPickup.isChecked -> {
                    orderToPlace.deliveryMethod = "Pickup"
                    orderToPlace.paid = false
                    showDialogDoubleConfirmation(orderToPlace)

                }
                radioButtonNotPaid.isChecked -> {
                    if (!hasUserProvidedAddress) {
                        showAddressWarningDialog()
                        return@setOnClickListener
                    }
                    orderToPlace.deliveryMethod = "Delivery unpaid"
                    orderToPlace.paid = false
                    showDialogDoubleConfirmation(orderToPlace)
                }
                radioButtonPaid.isChecked -> {
                    if (!hasUserProvidedAddress) {
                        showAddressWarningDialog()
                        return@setOnClickListener
                    }
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

    /**INFORMATION DIALOG WHICH INFORMS IF ORDER WAS PLACED SUCCESFULLY
     * IN CASE PARAMETER BOOLEAN WAS TRUE SNAPSHOTLISTENER GETS REMOVED*/
    fun showDialog(deleteSnapshot: Boolean) {
         val dialog = AlertDialog.Builder(requireContext())
            .setMessage("Your order has been placed successfully.")
            .setOnDismissListener { // if true remove snapshot
             if (deleteSnapshot)  snapshotListener.remove()
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
    fun showDialogDoubleConfirmation(order: Order){
        val dialog =   AlertDialog.Builder(requireContext())
        .setMessage("I order with an obligation to pay")
        .setPositiveButton("Proceed", null)
        .setNegativeButton("Cancel",null)
        .show()

        dialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener {
            placeOrder(order)
            cleanShoppingCart()
            showDialog(false)
            dialog.dismiss()
        }

    }

    fun showAddressWarningDialog(){
        AlertDialog.Builder(requireContext())
            .setMessage("You must provide address for your account")
            .setNegativeButton("Back",null)
            .show()
    }

    fun startLoading(){
        progressBar.visibility = View.VISIBLE
        loadingText.visibility = View.VISIBLE
    }

    fun stopLoading(){
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

