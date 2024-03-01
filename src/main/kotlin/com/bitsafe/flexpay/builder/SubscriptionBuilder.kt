package com.bitsafe.flexpay.builder

import com.bitsafe.flexpay.*
import com.bitsafe.flexpay.enums.PaymentMethod
import com.bitsafe.flexpay.enums.SaleCurrency
import com.bitsafe.flexpay.enums.SubscriptionType
import java.math.BigDecimal
import java.net.URL
import kotlin.error

class SubscriptionBuilder(private val flexpay: FlexPayClient) {
    private var period: String? = null
    private var subscriptionType: SubscriptionType? = null
    private var trialAmount: BigDecimal? = null
    private var trialPeriod: String? = null
    private var description: String? = null
    private var priceAmount: BigDecimal? = null
    private var priceCurrency: SaleCurrency? = null
    private var paymentMethod: PaymentMethod? = null
    private var referenceID: String? = null
    private var custom1: String? = null
    private var custom2: String? = null
    private var custom3: String? = null
    private var successURL: String? = null
    private var declineURL: String? = null
    private var email: String? = null
    private var version: String = FLEXPAY_VERSION

    fun withAmount(withAmount: BigDecimal, withCurrency: SaleCurrency): SubscriptionBuilder {
        priceAmount = withAmount
        priceCurrency = withCurrency

        return this
    }

    fun withPeriod(withPeriod: String): SubscriptionBuilder {
        period = withPeriod

        return this
    }

    fun withSubscriptionType(withSubscriptionType: SubscriptionType): SubscriptionBuilder {
        subscriptionType = withSubscriptionType

        return this
    }

    fun withTrialAmount(withTrialAmount: BigDecimal): SubscriptionBuilder {
        trialAmount = withTrialAmount

        return this
    }

    fun withTrialPeriod(withTrialPeriod: String): SubscriptionBuilder {
        trialPeriod = withTrialPeriod

        return this
    }

    fun withDescription(withDescription: String): SubscriptionBuilder {
        description = withDescription

        return this
    }

    fun withPaymentMethod(withPaymentMethod: PaymentMethod): SubscriptionBuilder {
        paymentMethod = withPaymentMethod

        return this
    }

    fun withReferenceID(withReferenceID: String): SubscriptionBuilder {
        referenceID = withReferenceID

        return this
    }

    fun withCustom1(withCustom1: String): SubscriptionBuilder {
        custom1 = withCustom1

        return this
    }

    fun withCustom2(withCustom2: String): SubscriptionBuilder {
        custom2 = withCustom2

        return this
    }

    fun withCustom3(withCustom3: String): SubscriptionBuilder {
        custom3 = withCustom3

        return this
    }

    fun withSuccessURL(withSuccessURL: String): SubscriptionBuilder {
        successURL = withSuccessURL

        return this
    }

    fun withDeclineURL(withDeclineURL: String): SubscriptionBuilder {
        declineURL = withDeclineURL

        return this
    }

    fun withEmail(withEmail: String): SubscriptionBuilder {
        email = withEmail

        return this
    }

    fun withVersion(withVersion: String): SubscriptionBuilder {
        version = withVersion

        return this
    }

    fun build(): URL {
        return flexpay.getSubscriptionUrl(
            period = period ?: error("Period must be set"),
            subscriptionType = subscriptionType ?: error("Subscription type must be set"),
            trialAmount = trialAmount,
            trialPeriod = trialPeriod,
            description = description,
            priceAmount = priceAmount ?: error("Price amount must be set"),
            priceCurrency = priceCurrency ?: error("Price currency must be set"),
            paymentMethod = paymentMethod,
            referenceID = referenceID,
            custom1 = custom1,
            custom2 = custom2,
            custom3 = custom3,
            successURL = successURL,
            declineURL = declineURL,
            email = email,
            version = version,
        )
    }
}