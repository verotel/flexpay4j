package com.bitsafe.flexpay.enums

/**
 * How to deal with the remaining period from a previous sale
 */
enum class UpgradeOption {
    /**
     * Remaining period is lost
     */
    lost,

    /**
     * Remaining period is added to the new sale
     */
    extend
}