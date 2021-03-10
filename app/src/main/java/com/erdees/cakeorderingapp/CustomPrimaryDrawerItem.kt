package com.erdees.cakeorderingapp

import android.app.Activity
import android.app.Application
import android.view.InflateException
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.materialdrawer.holder.ColorHolder
import com.mikepenz.materialdrawer.model.AbstractBadgeableDrawerItem
import com.mikepenz.materialdrawer.model.AbstractDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import java.util.zip.Inflater


class CustomPrimaryDrawerItem(override val layoutRes: Int, override val type: Int) : AbstractDrawerItem<Any,RecyclerView.ViewHolder>() {

    override fun getViewHolder(v: View): RecyclerView.ViewHolder {
        return object: RecyclerView.ViewHolder(v){

        }
    }


}

