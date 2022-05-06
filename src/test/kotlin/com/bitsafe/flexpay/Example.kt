package com.bitsafe.flexpay

import org.junit.jupiter.api.Test

class Example {

    @Test
    fun example() {
        val flexPayClient = FlexPayClient(
            websiteId = 65147,
            signatureKey = "mySecret",
            brand = Brand.VEROTEL
        )

        val purchaseUrl = flexPayClient.getPurchaseUrl(
            priceAmount = "25.99".toBigDecimal(),
            priceCurrency = SaleCurrency.EUR,
            description = "Extra comfy XL pyjamas",
        )

        // You can pay here
        println(purchaseUrl)
    }

}