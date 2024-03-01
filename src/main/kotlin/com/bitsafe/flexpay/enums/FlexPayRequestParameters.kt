package com.bitsafe.flexpay.enums

enum class FlexPayRequestParameters(val isSigned: Boolean, val flexPayName: String? = null) {
    version(isSigned = true),
    shopID(isSigned = true),
    priceAmount(isSigned = true),
    priceCurrency(isSigned = true),
    paymentMethod(isSigned = true),
    description(isSigned = true),
    referenceID(isSigned = true),
    saleID(isSigned = true),
    custom1(isSigned = true),
    custom2(isSigned = true),
    custom3(isSigned = true),
    subscriptionType(isSigned = true),
    period(isSigned = true),
    descriptionForSubscription(isSigned = true, flexPayName = "name"),
    trialAmount(isSigned = true),
    trialPeriod(isSigned = true),
    cancelDiscountPercentage(isSigned = true),
    type(isSigned = true),
    successURL(isSigned = true),
    declineURL(isSigned = true),
    precedingSaleID(isSigned = true),
    upgradeOption(isSigned = true),
    signature(isSigned = false),
    email(isSigned = false),
    oneClickToken(isSigned = false);

    val value = flexPayName ?: name
}