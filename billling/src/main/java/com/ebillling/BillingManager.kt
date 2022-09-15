package com.ebillling

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.android.billingclient.api.*

class BillingManager(
    private val context: Context,
    private val productList: ArrayList<String>,
    private val productType: ProductType,
    private val listener: BillingListener? = null
) : BillingClientStateListener {

    private var productDetailsListCache = emptyList<ProductDetails>()

    private val handler by lazy {
        Handler(Looper.getMainLooper())
    }

    private val billingClient: BillingClient by lazy {
        BillingClient.newBuilder(context)
            .setListener { billingResult: BillingResult, list: List<Purchase?>? ->
                checkBillingResult(billingResult, list)
            }
            .enablePendingPurchases()
            .build()
    }

    private fun checkBillingResult(
        billingResult: BillingResult,
        list: List<Purchase?>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            if (list != null) {
                for (purchase in list) {
                    purchase ?: continue
                    acknowledgePurchase(purchase)
                }
            }
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) return
        if (purchase.isAcknowledged) {
            checkPurchased(purchase)
            return
        }
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult: BillingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                checkPurchased(purchase)
            }
        }
    }

    private fun checkPurchased(purchase: Purchase) {
        val products = purchase.products
        if (products.isNotEmpty()) {
            handler.post { listener?.onBillingSuccessfulPurchased(products.first(), purchase) }
        }
    }

    fun startConnection() {
        billingClient.startConnection(this)
    }

    override fun onBillingServiceDisconnected() {
        handler.post { listener?.onBillingError(BillingError.DISCONNECTED) }
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        val newProductList = mutableListOf<QueryProductDetailsParams.Product>()
        for (productId in productList) {
            if (productId.isEmpty()) continue
            newProductList.add(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(ProductType::type.get(productType))
                    .build()
            )
        }
        val params = QueryProductDetailsParams.newBuilder().setProductList(newProductList)

        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(ProductType::type.get(productType))
                .build()
        ) { _, purchaseList ->
            billingClient.queryProductDetailsAsync(params.build())
            { _, productDetailsList ->
                if (productDetailsList.isNotEmpty()) {
                    productDetailsListCache = productDetailsList
                    for (product in productDetailsList) {
                        handler.post { listener?.onBillingPrice(product.productId, product) }
                    }
                }
                listener?.onBillingStartCheckPurchase()
                if (purchaseList.isNotEmpty()) {
                    for (purchase in purchaseList) {
                        val products = purchase.products
                        if (products.isNotEmpty()) {
                            handler.post {
                                listener?.onBillingPurchased(
                                    products.first(),
                                    purchase
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun buy(activity: Activity, productId: String) {
        if (productDetailsListCache.isEmpty()) {
            handler.post { listener?.onBillingError(BillingError.BUY_PRODUCT_LIST_EMPTY) }
            return
        }
        val product = productDetailsListCache.firstOrNull { it.productId == productId }
        if (product == null) {
            handler.post { listener?.onBillingError(BillingError.BUY_NOT_FOUND_PRODUCT_ID) }
            return
        }
        val offerToken = product.subscriptionOfferDetails?.firstOrNull()?.offerToken.toString()
        val productDetailsParamsList =
            listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(product)
                    .setOfferToken(offerToken)
                    .build()
            )
        val billingFlowParams =
            BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()
        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

}