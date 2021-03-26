package androidx.recyclerview.widget.com.erdees.cakeorderingapp.activities

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.fragments.AdminFragment
import androidx.recyclerview.widget.com.erdees.cakeorderingapp.fragments.AdminOrdersFragment
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.openFragment

class AdminActivity: AppCompatActivity() {

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) this.finish()
        super.onBackPressed()
    }

        val adminFragment = AdminFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_activity)


        openFragment(
            adminFragment,
            AdminFragment.TAG,
            supportFragmentManager,
            R.id.admin_container
        )

    }
    companion object {
        const val TAG = "AdminActivity"
    }
}