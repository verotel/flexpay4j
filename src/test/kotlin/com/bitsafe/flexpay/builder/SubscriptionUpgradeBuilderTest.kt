package com.bitsafe.flexpay.builder

import com.bitsafe.flexpay.*
import com.bitsafe.flexpay.enums.PaymentMethod
import com.bitsafe.flexpay.enums.SaleCurrency
import com.bitsafe.flexpay.enums.SubscriptionType
import com.bitsafe.flexpay.enums.UpgradeOption
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.math.BigDecimal

internal class SubscriptionUpgradeBuilderTest {
    @Test
    fun `builds subscription URL`() {
        val flexpay = FlexPayClient(websiteId = 7, signatureKey = "xx")
        val urlFromBuilder = flexpay.subscriptionUpgradeBuilder()
            .withPrecedingSaleID("42")
            .withPeriod("P3D")
            .withSubscriptionType(SubscriptionType.recurring)
            .withTrialAmount(BigDecimal.ONE)
            .withTrialPeriod("P1D")
            .withDescription("funny subscription")
            .withAmount(BigDecimal.TEN, SaleCurrency.USD)
            .withPaymentMethod(PaymentMethod.DDEU)
            .withCustom1("cus1")
            .withCustom2("cus2")
            .withCustom3("cus3")
            .withSuccessURL("success.url")
            .withEmail("email")
            .withVersion(FLEXPAY_VERSION)
            .withDeclineURL("https://example.com/decline")
            .withUpgradeOption(UpgradeOption.lost)
            .build()

        val urlFromMethod = flexpay.getUpgradeSubscriptionUrl(
            precedingSaleID = "42",
            period = "P3D",
            subscriptionType = SubscriptionType.recurring,
            trialAmount = BigDecimal.ONE,
            trialPeriod = "P1D",
            description = "funny subscription",
            priceAmount = BigDecimal.TEN,
            priceCurrency = SaleCurrency.USD,
            paymentMethod = PaymentMethod.DDEU,
            custom1 = "cus1",
            custom2 = "cus2",
            custom3 = "cus3",
            successURL = "success.url",
            email = "email",
            version = FLEXPAY_VERSION,
            declineURL = "https://example.com/decline",
            upgradeOption = UpgradeOption.lost,
        )

        assertThat(urlFromMethod).isEqualTo(urlFromBuilder)
    }
}