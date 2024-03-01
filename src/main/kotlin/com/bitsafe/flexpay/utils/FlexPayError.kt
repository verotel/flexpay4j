package com.bitsafe.flexpay.utils

import com.bitsafe.flexpay.FlexPayException

fun flexPayError(message: String): Nothing = throw FlexPayException(message)