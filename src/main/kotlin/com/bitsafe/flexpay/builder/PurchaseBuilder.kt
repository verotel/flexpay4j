package com.bitsafe.flexpay.builder

import com.bitsafe.flexpay.FLEXPAY_VERSION
import com.bitsafe.flexpay.FlexPayClient
import com.bitsafe.flexpay.enums.PaymentMethod
import com.bitsafe.flexpay.enums.SaleCurrency
import java.math.BigDecimal
import java.net.URL

class PurchaseBuilder(private val flexpay: FlexPayClient) {
    private var priceAmount: BigDecimal? = null
    private var priceCurrency: SaleCurrency? = null
    private var description: String? = null
    private var paymentMethod: PaymentMethod? = null
    private var referenceID: String? = null
    private var custom1: String? = null
    private var custom2: String? = null
    private var custom3: String? = null
    private var successURL: String? = null
    private var declineURL: String? = null
    private var oneClickToken: String? = null
    private var email: String? = null
    private var version: String = FLEXPAY_VERSION

    fun withAmount(withAmount: BigDecimal, withCurrency: SaleCurrency): PurchaseBuilder {
        priceAmount = withAmount
        priceCurrency = withCurrency

        return this
    }

    fun withDescription(withDescription: String): PurchaseBuilder {
        description = withDescription

        return this
    }

    fun withPaymentMethod(withPaymentMethod: PaymentMethod): PurchaseBuilder {
        paymentMethod = withPaymentMethod

        return this
    }

    fun withReferenceID(withReferenceID: String): PurchaseBuilder {
        referenceID = withReferenceID

        return this
    }

    fun withCustom1(withCustom1: String): PurchaseBuilder {
        custom1 = withCustom1

        return this
    }

    fun withCustom2(withCustom2: String): PurchaseBuilder {
        custom2 = withCustom2

        return this
    }

    fun withCustom3(withCustom3: String): PurchaseBuilder {
        custom3 = withCustom3

        return this
    }

    /**
     * Formerly known as backURL, successURL is a URL to which a buyer is redirected
     * after a transaction has been successfully paid
     */
    fun withSuccessURL(withSuccessURL: String): PurchaseBuilder {
        successURL = withSuccessURL

        return this
    }

    fun withDeclineURL(withDeclineURL: String): PurchaseBuilder {
        declineURL = withDeclineURL

        return this
    }

    fun withOneClickToken(withOneClickToken: String): PurchaseBuilder {
        oneClickToken = withOneClickToken

        return this
    }

    fun withEmail(withEmail: String): PurchaseBuilder {
        email = withEmail

        return this
    }

    fun withVersion(withVersion: String): PurchaseBuilder {
        version = withVersion

        return this
    }

    fun build(): URL {
        return flexpay.getPurchaseUrl(
            priceAmount = priceAmount ?: error("Price amount must be set"),
            priceCurrency = priceCurrency ?: error("Price currency must be set"),
            description = description ?: error("Description must be set"),
            paymentMethod = paymentMethod,
            referenceID = referenceID,
            custom1 = custom1,
            custom2 = custom2,
            custom3 = custom3,
            successURL = successURL,
            declineURL = declineURL,
            oneClickToken = oneClickToken,
            email = email,
            version = version,
        )
    }
}