package androidx.recyclerview.widget.com.erdees.cakeorderingapp.stripe

import android.app.Application
import com.stripe.android.PaymentConfiguration

class FirebaseMobilePaymentApp : Application(){
    override fun onCreate() {
        super.onCreate()
        PaymentConfiguration.init(
            applicationContext,
            "pk_test_51IWNZYFWPZNf15isNRfq1xDAm8IrX0Rs4AHQJWkzpHiUPmtjB2gubJFlkxUi8v3QN1DvrYFztoyYI67khp499F7y005giE0ss6"

        )
    }
}
