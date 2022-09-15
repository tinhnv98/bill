package com.ebillling

import android.content.Context

class BillingCreateBuilder : BillingBuilder {

    private var productList: ArrayList<String>? = null
    private var productType: ProductType? = null
    private var listener: BillingListener? = null

    override fun setProductList(productList: ArrayList<String>): BillingBuilder {
        this.productList = productList
        return this
    }

    override fun setProductType(productType: ProductType): BillingBuilder {
        this.productType = productType
        return this
    }

    override fun setBillingListener(listener: BillingListener): BillingBuilder {
        this.listener = listener
        return this
    }

    override fun builder(context: Context): BillingManager {
        if (productList.isNullOrEmpty()) throw NullPointerException("productList null or empty")
        if (productType == null) throw NullPointerException("productType null")
        return BillingManager(context, productList!!, productType!!, listener)
    }
}