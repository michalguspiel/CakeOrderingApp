package com.erdees.cakeorderingapp.adapter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.fragments.EachProductFragment
import com.erdees.cakeorderingapp.fragments.ProductsFragment
import com.erdees.cakeorderingapp.model.PresentedItem
import com.erdees.cakeorderingapp.model.Products
import com.erdees.cakeorderingapp.openFragment
import com.erdees.cakeorderingapp.viewmodel.MainActivityRecyclerAdapterViewModel
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainActivityRecyclerAdapter(
    private val activity: Activity,
      options: FirestorePagingOptions<PresentedItem>, private val screenWidth: Int,
    private val supportFragmentManager: FragmentManager,
    val viewModel : MainActivityRecyclerAdapterViewModel
) :
    FirestorePagingAdapter<PresentedItem, MainActivityRecyclerAdapter.PresentedItemViewHolder>(
        options
    ) {

    val db = Firebase.firestore
    val eachProductFragment =  EachProductFragment()
    val productsFragment = ProductsFragment()
    companion object {
        private const val normalItem = 1
        private const val storePresentation = 0
        private const val morePastries = 2
        private const val contactItem = 3

    }

    class PresentedItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    private fun sendMail(address: Array<String> ) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, address)
        intent.putExtra(Intent.EXTRA_SUBJECT, "subject")
        startActivity(this.activity,intent,null)
    }


    override fun onBindViewHolder(
        holder: MainActivityRecyclerAdapter.PresentedItemViewHolder,
        position: Int,
        model: PresentedItem
    ) {
        when (getItemViewType(position)) {
            normalItem -> {
                val image = holder.itemView.findViewById<ImageView>(R.id.product_image)
                val name = holder.itemView.findViewById<TextView>(R.id.product_name)
                val desc = holder.itemView.findViewById<TextView>(R.id.product_desc)
                val button = holder.itemView.findViewById<Button>(R.id.main_recycler_more)
                name.text = model.name
                desc.text = model.description
                Glide.with(activity)
                    .load(model.pictureUrl)
                    .override(screenWidth, 400)
                    .centerCrop()
                    .into(image)
                button.setOnClickListener {
                    val productDocument = db.collection("products").document(model.productId).get()


                }
            }
            storePresentation -> {
                val recyclerView =
                    holder.itemView.findViewById<RecyclerView>(R.id.horizontal_recycler)
                recyclerView.adapter = HorizontalAdapter(model.picturesArray, activity, screenWidth)
                val linearLayoutManager =
                    LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                recyclerView.layoutManager = linearLayoutManager
            }
            contactItem -> {
                val title =
                    holder.itemView.findViewById<TextView>(R.id.title_contanct_recycler_item)
                val desc = holder.itemView.findViewById<TextView>(R.id.desc_contact_recycler_item)
                val mailBtn =
                    holder.itemView.findViewById<Button>(R.id.mail_button_contact_recycler_item)
                title.text = model.name
                desc.text = model.description
                mailBtn.text = model.mail
                mailBtn.setOnClickListener {
                    sendMail(arrayOf("guspielmichal@gmail.com"))
                }

            }
            morePastries -> {
                val image = holder.itemView.findViewById<ImageView>(R.id.more_pastries_image)
                Glide.with(activity)
                    .load(model.pictureUrl)
                    .override(screenWidth, 500)
                    .into(image)
                val window = holder.itemView.findViewById<LinearLayout>(R.id.more_pastries_window)
                window.setOnClickListener {
                    openFragment(productsFragment,ProductsFragment.TAG,supportFragmentManager)
                }

            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)?.get("type") as Long) {
            1L -> normalItem
            0L -> storePresentation
            3L -> contactItem
            else -> 2
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MainActivityRecyclerAdapter.PresentedItemViewHolder {
        when (viewType) {
            normalItem -> {
                val view = LayoutInflater.from(activity)
                    .inflate(R.layout.main_recycler_item, parent, false)
                return MainActivityRecyclerAdapter.PresentedItemViewHolder(view)
            }
            storePresentation -> {
                val view = LayoutInflater.from(activity)
                    .inflate(R.layout.horizontal_recycler_view, parent, false)
                return MainActivityRecyclerAdapter.PresentedItemViewHolder(view)
            }
            contactItem -> {
                val view = LayoutInflater.from(activity)
                    .inflate(R.layout.contact_recycler_item, parent, false)
                return PresentedItemViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(activity)
                    .inflate(R.layout.main_recycler_more_pastries, parent, false)
                return PresentedItemViewHolder(view)
            }
        }
    }
}




