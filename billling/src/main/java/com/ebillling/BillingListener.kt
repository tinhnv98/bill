package com.ebillling

import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase


enum class BillingError {
    DISCONNECTED,
    BUY_PRODUCT_LIST_EMPTY,
    BUY_NOT_FOUND_PRODUCT_ID
}

interface BillingListener {
    /**
     * Bắt đầu gửi yêu cầu kiểm tra kiểm tra IAP or SUB
     * */
    fun onBillingStartCheckPurchase()

    /**
     * Được gọi khi yêu cầu kiểm tra IAP or SUB trả về kết quả
     * */
    fun onBillingPurchased(productId: String, purchase: Purchase)

    /**
     * Được gọi khi người dùng BUY thành công
     * */
    fun onBillingSuccessfulPurchased(productId: String, purchase: Purchase)

    /**
     * Trả về ProductDetails
     * */
    fun onBillingPrice(productId: String, productDetails: ProductDetails)

    /**
     * Được gọi khi có lỗi sẽ trả về BillingError tương ứng
     * */
    fun onBillingError(billingError: BillingError)
}