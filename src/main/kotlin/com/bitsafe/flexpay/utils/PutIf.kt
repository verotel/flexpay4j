package com.bitsafe.flexpay.utils

fun MutableParamsMap.putIf(key: String, value: String, condition: Boolean) {
    if (condition) {
        put(key, value)
    }
}