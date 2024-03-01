package com.bitsafe.flexpay.enums

enum class UrlType(val isPartOfUrl: Boolean) {
    PURCHASE(isPartOfUrl = true),
    SUBSCRIPTION(isPartOfUrl = true),
    UPGRADESUBSCRIPTION(isPartOfUrl = true),
    STATUS(isPartOfUrl = false),
    CANCEL_SUBSCRIPTION(isPartOfUrl = false);

    val nameForUrl = name.lowercase()
}