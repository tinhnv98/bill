package com.ebilling

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.ebillling.*

class MainActivity : AppCompatActivity(), BillingListener {

    val billingManager by lazy {
        BillingCreateBuilder()
            .setProductType(ProductType.SUBS)
            .setProductList(arrayListOf())
            .setBillingListener(this)
            .builder(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        billingManager.startConnection()
    }

    override fun onBillingStartCheckPurchase() {

    }

    override fun onBillingPurchased(productId: String, purchase: Purchase) {

    }

    override fun onBillingSuccessfulPurchased(productId: String, purchase: Purchase) {

    }

    override fun onBillingPrice(productId: String, productDetails: ProductDetails) {

    }

    override fun onBillingError(billingError: BillingError) {

    }

}