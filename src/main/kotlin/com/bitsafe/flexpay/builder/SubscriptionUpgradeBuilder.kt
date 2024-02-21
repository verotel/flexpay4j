package com.bitsafe.flexpay.builder

import com.bitsafe.flexpay.*
import java.math.BigDecimal
import java.net.URL
import kotlin.error

class SubscriptionUpgradeBuilder(private val flexpay: FlexPayClient) {
    private var precedingSaleID: String? = null
    private var period: String? = null
    private var subscriptionType: SubscriptionType? = null
    private var trialAmount: BigDecimal? = null
    private var trialPeriod: String? = null
    private var description: String? = null
    private var priceAmount: BigDecimal? = null
    private var priceCurrency: SaleCurrency? = null
    private var paymentMethod: PaymentMethod? = null
    private var custom1: String? = null
    private var custom2: String? = null
    private var custom3: String? = null
    private var successURL: String? = null
    private var email: String? = null
    private var version: String = FLEXPAY_VERSION

    fun withAmount(withAmount: BigDecimal, withCurrency: SaleCurrency): SubscriptionUpgradeBuilder {
        priceAmount = withAmount
        priceCurrency = withCurrency

        return this
    }

    fun withPrecedingSaleID(withPrecedingSaleID: String): SubscriptionUpgradeBuilder {
        precedingSaleID = withPrecedingSaleID

        return this
    }

    fun withPeriod(withPeriod: String): SubscriptionUpgradeBuilder {
        period = withPeriod

        return this
    }

    fun withSubscriptionType(withSubscriptionType: SubscriptionType): SubscriptionUpgradeBuilder {
        subscriptionType = withSubscriptionType

        return this
    }

    fun withTrialAmount(withTrialAmount: BigDecimal): SubscriptionUpgradeBuilder {
        trialAmount = withTrialAmount

        return this
    }

    fun withTrialPeriod(withTrialPeriod: String): SubscriptionUpgradeBuilder {
        trialPeriod = withTrialPeriod

        return this
    }

    fun withDescription(withDescription: String): SubscriptionUpgradeBuilder {
        description = withDescription

        return this
    }

    fun withPaymentMethod(withPaymentMethod: PaymentMethod): SubscriptionUpgradeBuilder {
        paymentMethod = withPaymentMethod

        return this
    }

    fun withCustom1(withCustom1: String): SubscriptionUpgradeBuilder {
        custom1 = withCustom1

        return this
    }

    fun withCustom2(withCustom2: String): SubscriptionUpgradeBuilder {
        custom2 = withCustom2

        return this
    }

    fun withCustom3(withCustom3: String): SubscriptionUpgradeBuilder {
        custom3 = withCustom3

        return this
    }

    fun withSuccessURL(withSuccessURL: String): SubscriptionUpgradeBuilder {
        successURL = withSuccessURL

        return this
    }

    fun withEmail(withEmail: String): SubscriptionUpgradeBuilder {
        email = withEmail

        return this
    }

    fun withVersion(withVersion: String): SubscriptionUpgradeBuilder {
        version = withVersion

        return this
    }

    fun build(): URL {
        return flexpay.getUpgradeSubscriptionUrl(
            precedingSaleID = precedingSaleID ?: error("Preceding sale ID must be set"),
            period = period ?: error("Period must be set"),
            subscriptionType = subscriptionType ?: error("Subscription type must be set"),
            trialAmount = trialAmount,
            trialPeriod = trialPeriod,
            description = description,
            priceAmount = priceAmount ?: error("Price amount must be set"),
            priceCurrency = priceCurrency ?: error("Price currency must be set"),
            paymentMethod = paymentMethod,
            custom1 = custom1,
            custom2 = custom2,
            custom3 = custom3,
            successURL = successURL,
            email = email,
            version = version,
        )
    }
}