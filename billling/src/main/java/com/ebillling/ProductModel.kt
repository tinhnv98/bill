package com.ebillling

import com.android.billingclient.api.BillingClient

enum class ProductType(val type: String) {
    SUBS(BillingClient.ProductType.SUBS),
    INAPP(BillingClient.ProductType.INAPP)
}

data class ProductModel(
    val productId: String = "",
    val productType: ProductType = ProductType.SUBS
)