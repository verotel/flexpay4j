package com.bitsafe.flexpay.builder

import com.bitsafe.flexpay.FLEXPAY_VERSION
import com.bitsafe.flexpay.FlexPayClient
import com.bitsafe.flexpay.PaymentMethod
import com.bitsafe.flexpay.SaleCurrency
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PurchaseBuilderTest {

    @Test
    fun `builds purchase URL`() {
        val flexpay = FlexPayClient(7, "xx")
        val urlFromMethod = flexpay.getPurchaseUrl(
            priceAmount = "14".toBigDecimal(),
            priceCurrency = SaleCurrency.EUR,
            description = "test description",
            paymentMethod = PaymentMethod.CC,
            referenceID = "ref1",
            custom1 = "cus1",
            custom2 = "cus2",
            custom3 = "cus3",
            backURL = "back.url",
            declineURL = "decline.url",
            oneClickToken = "tok",
            email = "email",
            version = FLEXPAY_VERSION,
        )
        val urlFromBuilder = flexpay.purchaseBuilder()
            .withAmount("14".toBigDecimal(), SaleCurrency.EUR)
            .withDescription("test description")
            .withPaymentMethod(PaymentMethod.CC)
            .withReferenceID("ref1")
            .withCustom1("cus1")
            .withCustom2("cus2")
            .withCustom3("cus3")
            .withBackURL("back.url")
            .withDeclineURL("decline.url")
            .withOneClickToken("tok")
            .withEmail("email")
            .withVersion(FLEXPAY_VERSION)
            .build()

        assertThat(urlFromBuilder).isEqualTo(urlFromMethod)
    }

}