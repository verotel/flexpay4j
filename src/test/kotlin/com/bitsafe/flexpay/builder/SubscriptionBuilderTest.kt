package com.bitsafe.flexpay.builder

import com.bitsafe.flexpay.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.math.BigDecimal

internal class SubscriptionBuilderTest {
    @Test
    fun `builds subscription URL`() {
        val flexpay = FlexPayClient(7, "xx")
        val urlFromBuilder = flexpay.subscriptionBuilder()
            .withPeriod("P3D")
            .withSubscriptionType(SubscriptionType.recurring)
            .withTrialAmount(BigDecimal.ONE)
            .withTrialPeriod("P1D")
            .withDescription("funny subscription")
            .withAmount(BigDecimal.TEN, SaleCurrency.USD)
            .withPaymentMethod(PaymentMethod.DDEU)
            .withReferenceID("ref1")
            .withCustom1("cus1")
            .withCustom2("cus2")
            .withCustom3("cus3")
            .withSuccessURL("success.url")
            .withDeclineURL("decline.url")
            .withEmail("email")
            .withVersion(FLEXPAY_VERSION)
            .build()

        val urlFromMethod = flexpay.getSubscriptionUrl(
            period = "P3D",
            subscriptionType = SubscriptionType.recurring,
            trialAmount = BigDecimal.ONE,
            trialPeriod = "P1D",
            description = "funny subscription",
            priceAmount = BigDecimal.TEN,
            priceCurrency = SaleCurrency.USD,
            paymentMethod = PaymentMethod.DDEU,
            referenceID = "ref1",
            custom1 = "cus1",
            custom2 = "cus2",
            custom3 = "cus3",
            successURL = "success.url",
            declineURL = "decline.url",
            email = "email",
            version = FLEXPAY_VERSION,
        )

        assertThat(urlFromMethod).isEqualTo(urlFromBuilder)
    }
}