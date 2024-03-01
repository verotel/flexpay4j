package com.bitsafe.flexpay

import com.bitsafe.flexpay.utils.flexPayError

enum class Brand(val BASE_URL: String) {
    VEROTEL("https://secure.verotel.com"),
    BITSAFEPAY("https://secure.bitsafepay.com"),
    CARDBILLING("https://secure.billing.creditcard"),
    GAYCHARGE("https://secure.gaycharge.com"),
    PAINTFEST("https://secure.paintfestpayments.com"),
    BILL("https://secure.bill.creditcard"),
    YOURSAFE_DIRECT("https://secure.yoursafedirect.com");

    val FLEXPAY_PATH = "/startorder"
    val STATUS_PATH = "/salestatus"
    val CANCEL_PATH = "/cancel-subscription"

    val flexPayUrl = "$BASE_URL$FLEXPAY_PATH"
    val statusUrl = "$BASE_URL$STATUS_PATH"
    val cancelUrl = "$BASE_URL$CANCEL_PATH"

    companion object {
        private val brandByMerchantPrefix = mapOf(
            "9804" to VEROTEL,
            "9762" to CARDBILLING,
            "9653" to BITSAFEPAY,
            "9511" to BILL,
            "9444" to PAINTFEST,
            "9388" to GAYCHARGE,
            "9001" to YOURSAFE_DIRECT,
        )

        fun fromMerchantId(merchantID: String): Brand {
            val merchantPrefix = merchantID.take(4)

            return brandByMerchantPrefix.getOrElse(merchantPrefix) {
                flexPayError("Invalid merchant ID")
            }
        }

        fun fromName(brandName: String): Brand {
            return brandByMerchantPrefix.values.singleOrNull {
                it.name.lowercase() == brandName.lowercase()
            } ?: flexPayError("Invalid brand name")
        }
    }
}
