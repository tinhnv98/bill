package com.ebillling

import android.content.Context

interface BillingBuilder {
    fun setProductList(productList: ArrayList<String>): BillingBuilder
    fun setProductType(productType: ProductType): BillingBuilder
    fun setBillingListener(listener: BillingListener): BillingBuilder
    fun builder(context: Context): BillingManager
}