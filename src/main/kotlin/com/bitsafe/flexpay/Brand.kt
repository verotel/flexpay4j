package com.bitsafe.flexpay

enum class Brand(val BASE_URL: String) {
    VEROTEL("https://secure.verotel.com"),
    BITSAFEPAY("https://secure.bitsafepay.com"),
    CARDBILLING("https://secure.billing.creditcard"),
    GAYCHARGE("https://secure.gaycharge.com"),
    PAINTFEST("https://secure.paintfestpayments.com"),
    BILL("https://secure.bill.creditcard");

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
        )

        fun fromMerchantId(merchantID: String): Brand {
            val merchantPrefix = merchantID.take(4)

            return brandByMerchantPrefix.getOrElse(merchantPrefix) {
                error("Invalid merchant ID")
            }
        }

        fun fromName(brandName: String): Brand {
            return brandByMerchantPrefix.values.singleOrNull {
                it.name.lowercase() == brandName.lowercase()
            } ?: error("Invalid brand name")
        }
    }
}
