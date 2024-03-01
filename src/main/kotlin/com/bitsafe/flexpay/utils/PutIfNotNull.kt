package com.bitsafe.flexpay.utils

fun MutableParamsMap.putIfNotNull(key: String, value: String?) {
    if (value != null) {
        put(key, value)
    }
}