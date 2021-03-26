package androidx.recyclerview.widget.com.erdees.cakeorderingapp.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.adapter.AdminOrdersRecyclerAdapter
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.adapter.MyOrdersRecyclerAdapter
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.viewmodel.AdminOrdersFragmentViewModel
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.viewmodel.MyOrdersRecyclerAdapterViewModel
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.makeGone
import com.erdees.cakeorderingapp.makeVisible
import com.erdees.cakeorderingapp.model.Order
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate

class AdminOrdersFragment: Fragment() {

    lateinit var activeButton : TextView
    lateinit var historyButton : TextView


    private lateinit var activeTableRow : TableRow
    private lateinit var historyTableRow : TableRow
    private lateinit var standardTextColor : ColorStateList
    private lateinit var recyclerView: RecyclerView
    private lateinit var activeAdapter : AdminOrdersRecyclerAdapter
    private lateinit var historyAdapter : MyOrdersRecyclerAdapter
    private lateinit var dateText : TextView
    private lateinit var previousDayButton : TextView
    private lateinit var nextDayButton : TextView
    private lateinit var viewModel : AdminOrdersFragmentViewModel
    private lateinit var changingDayLayout : LinearLayout
    private lateinit var ordersForTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.admin_orders_fragment,container,false)

        /**Binders*/
        recyclerView = view.findViewById(R.id.admin_orders_recycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)

         activeButton = view.findViewById(R.id.button_active)
         historyButton = view.findViewById(R.id.button_history)
        activeTableRow = view.findViewById(R.id.active_table_row)
        historyTableRow = view.findViewById(R.id.history_table_row)
        standardTextColor = activeButton.textColors // saving default text color
        dateText = view.findViewById(R.id.text_date)
        previousDayButton  = view.findViewById(R.id.previous_day_button)
        nextDayButton = view.findViewById(R.id.next_day_button)
        changingDayLayout = view.findViewById(R.id.admin_orders_linear_layout)
        ordersForTextView = view.findViewById(R.id.admin_orders_ordersFor_text)

        viewModel = ViewModelProvider(this).get(AdminOrdersFragmentViewModel::class.java)

        var exposedDay = LocalDate.now()
        viewModel.setPresentedDate(exposedDay) // To start with today
        dateText.text = exposedDay.toString()
        val db = Firebase.firestore
        val auth = Firebase.auth


        //THIS USER HISTORY ORDERS
        val historyQuery = db.collection("placedOrders")
            .whereEqualTo("orderStatus","completed")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50)
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(20)
            .setPageSize(15)
            .build()
        val historyOptions = FirestorePagingOptions.Builder<Order>()// History of orders is paging options because realtime updates doesn't matter
            .setLifecycleOwner(this)
            .setQuery(historyQuery,config, Order::class.java)
            .build()
        historyAdapter = MyOrdersRecyclerAdapter(historyOptions,requireActivity(),parentFragmentManager,ViewModelProvider(this).get(MyOrdersRecyclerAdapterViewModel::class.java),TAG)


        viewModel.getPresentedDate.observe(viewLifecycleOwner, Observer { date ->
            //THIS USER ACTIVE PLACED ORDERS
            val activeQuery = db.collection("placedOrders")
                .whereEqualTo("orderStatus","active")
                .whereEqualTo("orderToBeReadyDate",date.toString())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50)
            val activeOptions = FirestoreRecyclerOptions.Builder<Order>()
                .setLifecycleOwner(this)
                .setQuery(activeQuery, Order::class.java)
                .build()
                activeAdapter  = AdminOrdersRecyclerAdapter(activeOptions,requireContext(),requireActivity())
                recyclerView.adapter = activeAdapter

        })


        /**Buttons*/

        activeButton.setOnClickListener { activeChosen() }
        historyButton.setOnClickListener { historyChosen() }

        nextDayButton.setOnClickListener {
            exposedDay = exposedDay.plusDays(1L)
            viewModel.setPresentedDate(exposedDay)
            dateText.text = exposedDay.toString()
        }
        previousDayButton.setOnClickListener {
            exposedDay =  exposedDay.minusDays(1L)
            viewModel.setPresentedDate(exposedDay)
            dateText.text = exposedDay.toString()
        }

        activeButton.setTextColor(resources.getColor(R.color.pink_500))
        activeTableRow.isVisible = true
        historyButton.setTextColor(standardTextColor)
        historyTableRow.isVisible = false
        val activeQuery = db.collection("placedOrders")
            .whereEqualTo("orderStatus","active")
            .whereEqualTo("orderToBeReadyDate",exposedDay.toString())
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50)
        val activeOptions = FirestoreRecyclerOptions.Builder<Order>()
            .setLifecycleOwner(this)
            .setQuery(activeQuery, Order::class.java)
            .build()
        activeAdapter  = AdminOrdersRecyclerAdapter(activeOptions,requireContext(),requireActivity())
        recyclerView.adapter = activeAdapter
        return view
    }
    /**Functions*/
    fun activeChosen() {
        activeButton.setTextColor(resources.getColor(R.color.pink_500))
        activeTableRow.isVisible = true
        historyButton.setTextColor(standardTextColor)
        historyTableRow.isVisible = false
       recyclerView.adapter = activeAdapter
        changingDayLayout.makeVisible()
        ordersForTextView.makeVisible()
    }

    fun historyChosen() {
        activeButton.setTextColor(standardTextColor)
        activeTableRow.isVisible = false
        historyButton.setTextColor(resources.getColor(R.color.pink_500))
        historyTableRow.isVisible = true
        recyclerView.adapter = historyAdapter
        changingDayLayout.makeGone()
        ordersForTextView.makeGone()
    }


    companion object{
        const val TAG = "AdminOrdersFragment"
        fun newInstance(): AdminOrdersFragment = AdminOrdersFragment()
    }
}